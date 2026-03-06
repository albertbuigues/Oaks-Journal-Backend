package com.ortola.buigues.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpritesDto(
    val other: PokemonSpriteDto
)

@Serializable
data class PokemonSpriteDto(
    @SerialName("official-artwork")
    val officialArtwork: PokemonArtworkDto
)

@Serializable
data class PokemonArtworkDto(
    @SerialName("front_default")
    val frontDefault: String
)