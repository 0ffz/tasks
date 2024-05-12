package me.dvyy.tasks.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import me.dvyy.tasks.model.AppFormats

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json = AppFormats.json)
    }
}
