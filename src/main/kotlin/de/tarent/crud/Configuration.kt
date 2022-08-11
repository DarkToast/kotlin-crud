package de.tarent.crud

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream

data class Database(
    val connection: String,
    val driver: String,
    val username: String,
    val password: String
)

data class Configuration(val database: Database? = null) {
    companion object {
        fun load(configurationFile: String): Configuration? {
            val mapper = ObjectMapper(YAMLFactory())
            mapper.registerModule(KotlinModule.Builder().build())

            val stream: InputStream? =
                Configuration::class.java.getResourceAsStream(configurationFile)

            return stream?.bufferedReader(Charsets.UTF_8)?.use {
                mapper.readValue(it, Configuration::class.java)
            }
        }
    }
}