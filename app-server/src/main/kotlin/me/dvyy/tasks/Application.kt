package me.dvyy.tasks

import io.ktor.server.application.*
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.plugins.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database.connect(
        url = environment.config.property("database.url").getString(),
    )
    val server = ServerDataSource(database)
    val jwtConfig = JWTConfig(environment)
    val ldapConfig = LDAPConfig(environment)

    configureSecurity(jwtConfig)
    configureCORS()
    configureSerialization()
    configureRouting(
        server,
        jwtConfig,
        ldapConfig
    )
}
