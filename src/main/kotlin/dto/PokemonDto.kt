package com.ortola.buigues.dto

import kotlinx.serialization.Serializable

@Serializable
data class PokemonRawDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val cries: PokemonCriesDto,
    val types: List<PokemonTypesDto>,
    val sprites: PokemonSpritesDto
)

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    val heightInMeters: Double,
    val weightInKilos: Double,
    val cryLink: String,
    val type: List<String>,
    val spriteLink: String
)
