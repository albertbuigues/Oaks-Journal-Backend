package com.ortola.buigues

import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.PokemonDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    routing {
        get(path = "/pokemon") {
            try {
                val pokedexList = transaction {
                    PokemonTable.selectAll()
                        .orderBy(PokemonTable.id, SortOrder.ASC)
                        .map { row ->
                            PokemonDto(
                                id = row[PokemonTable.id],
                                name = row[PokemonTable.name],
                                heightInMeters = row[PokemonTable.height],
                                weightInKilos = row[PokemonTable.weight],
                                cryLink = row[PokemonTable.cryUrl],
                                spriteLink = row[PokemonTable.imageUrl],
                                type = row[PokemonTable.types].split(",")
                            )
                        }
                }
                if (pokedexList.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "No results found.")
                    return@get
                }
                call.respond(pokedexList)
            } catch (e: Exception) {
                println("Error serializing Pokemon: ${e.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Error accessing database")
            }
        }
    }
}
