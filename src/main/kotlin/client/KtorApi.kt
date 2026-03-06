package com.ortola.buigues.client

import com.ortola.buigues.dto.PokemonDto
import com.ortola.buigues.dto.PokemonRawDto
import com.ortola.buigues.saveToLocalDatabase
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

suspend fun fetchKantoPokemon() {
    println("🚀 INICIANT DESCÀRREGA DE LA POKEDEX (1 a 151)")
    val startTime = System.currentTimeMillis()
    val kantoRange = 1..151

    val data: List<PokemonDto> = kantoRange.mapNotNull { id ->
        val response = httpClient.get("https://pokeapi.co/api/v2/pokemon/$id")

        if (response.status == HttpStatusCode.OK) {
            val raw = response.body<PokemonRawDto>()

            // Creem el DTO
            val dto = PokemonDto(
                id = raw.id,
                name = raw.name.replaceFirstChar { it.uppercase() },
                heightInMeters = raw.height / 10.0,
                weightInKilos = raw.weight / 10.0,
                cryLink = raw.cries.latest,
                type = raw.types.sortedBy { it.slot }.map { it.type.name },
                spriteLink = raw.sprites.other.officialArtwork.frontDefault
            )

            val percent = (id * 100) / 151
            println("👾 [$percent%] - #$id ${dto.name} processat.")

            dto
        } else {
            println("⚠️ Error al descarregar ID #$id: ${response.status}")
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
}