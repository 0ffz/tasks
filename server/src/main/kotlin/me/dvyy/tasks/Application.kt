package me.dvyy.tasks

import io.ktor.server.application.*
import me.dvyy.tasks.plugins.configureDatabases
import me.dvyy.tasks.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
//    configureSecurity()
//    configureRouting()
}
