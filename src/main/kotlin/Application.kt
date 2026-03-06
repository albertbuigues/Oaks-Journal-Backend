package com.ortola.buigues

import com.ortola.buigues.client.fetchKantoPokemon
import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.PokemonDto
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()

    initDatabase()

    val pokemonCount = transaction {
        PokemonTable.selectAll().count()
    }

    if (pokemonCount != 151L) {
        launch(Dispatchers.IO) {
            transaction {
                PokemonTable.deleteAll()
                commit()
            }
            fetchKantoPokemon()
        }
    }
}

fun initDatabase() {
    Database.connect("jdbc:sqlite:./data.db", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(PokemonTable)
    }
}

fun saveToLocalDatabase(pokemonList: List<PokemonDto>) {
    transaction {
        PokemonTable.batchInsert(pokemonList) { pokemon ->
            this[PokemonTable.id] = pokemon.id
            this[PokemonTable.name] = pokemon.name
            this[PokemonTable.height] = pokemon.heightInMeters
            this[PokemonTable.weight] = pokemon.weightInKilos
            this[PokemonTable.types] = pokemon.type.joinToString(",")
            this[PokemonTable.cryUrl] = pokemon.cryLink
            this[PokemonTable.imageUrl] = pokemon.spriteLink
        }
    }
}
