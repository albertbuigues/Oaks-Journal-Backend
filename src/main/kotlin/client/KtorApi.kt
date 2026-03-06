package com.ortola.buigues.client

import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.PokemonDto
import com.ortola.buigues.dto.PokemonRawDto
import com.ortola.buigues.saveToLocalDatabase
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun fetchKantoPokemon() {
    val startTime = System.currentTimeMillis()
    val kantoRange = 1..151

    val data: List<PokemonDto> = kantoRange.mapNotNull { id ->
        val response = httpClient.get("https://pokeapi.co/api/v2/pokemon/$id")
        if (response.status == HttpStatusCode.OK) {
            val raw = response.body<PokemonRawDto>()
            val dto = PokemonDto(
                id = raw.id,
                name = raw.name.replaceFirstChar { it.uppercase() },
                heightInMeters = raw.height / 10.0,
                weightInKilos = raw.weight / 10.0,
                cryLink = raw.cries.latest,
                type = raw.types.sortedBy { it.slot }.map { it.type.name },
                spriteLink = raw.sprites.other.officialArtwork.frontDefault
            )
            dto
        } else {
            null
        }
    }

    if (data.size == 151) {
        saveToLocalDatabase(pokemonList = data)
        val duration = (System.currentTimeMillis() - startTime) / 1000
        println("🏁 FINALITZAT! 151 Pokémon guardats a la DB en $duration segons.")
    } else {
        println("❌ Error: Només s'han descarregat ${data.size} de 151 Pokémon.")
    }

    transaction {
        val count = PokemonTable.selectAll().count()
        println("DEBUG: Hay $count pokémon en la base de datos.")
    }
}