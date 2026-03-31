package com.ortola.buigues

import com.ortola.buigues.client.fetchKantoPokemon
import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.PokemonDto
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    java.net.NetworkInterface.getNetworkInterfaces().asSequence()
        .flatMap { it.inetAddresses.asSequence() }
        .filter { it is java.net.Inet4Address && !it.isLoopbackAddress }
        .forEach { println("🚀 Ktor podria estar disponible a: http://${it.hostAddress}:8080") }

    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

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
