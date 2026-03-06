package com.ortola.buigues.dto

import kotlinx.serialization.Serializable

@Serializable
data class PokemonTypesDto(
    val slot: Int,
    val type: PokemonTypeDto
)

@Serializable
data class PokemonTypeDto(
    val name: String
)
