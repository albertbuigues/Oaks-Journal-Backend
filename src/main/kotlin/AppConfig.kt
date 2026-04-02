package com.ortola.buigues

import java.util.Properties
import java.io.File

object AppConfig {
    private val properties = Properties()

    init {
        val propertiesFile = File("local.properties")
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { properties.load(it) }
        }
    }

    val geminiApiKey: String
        get() = properties.getProperty("GEMINI_API_KEY") ?: ""
}