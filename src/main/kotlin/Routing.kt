package com.ortola.buigues

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.ortola.buigues.ai.GenerativeAiManager
import com.ortola.buigues.database.PokemonTable
import com.ortola.buigues.dto.MessageToOakDto
import com.ortola.buigues.dto.PokemonDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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

        post(path = "/pokemon/ask") {
            try {
                val msg = call.receive<MessageToOakDto>().message
                val response = GenerativeAiManager.sendQuestionAndReceiveResponse(msg)
                response?.let { answer ->
                    call.respond(HttpStatusCode.OK, answer)
                }
            } catch (ex: ContentTransformationException) {
                println(ex.localizedMessage)
                call.respond(HttpStatusCode.InternalServerError, "Error receiving message")
            }
        }
    }
}
