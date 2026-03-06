package com.ortola.buigues.database

import org.jetbrains.exposed.sql.Table

object PokemonTable: Table("pokemon") {
    val id = integer("id")
    val name = varchar("name", 50)
    val height = double("height")
    val weight = double("weight")
    val cryUrl = varchar("cryUrl", 255)
    val types = varchar("types", 100)
    val imageUrl = varchar("imageUrl", 255)
}