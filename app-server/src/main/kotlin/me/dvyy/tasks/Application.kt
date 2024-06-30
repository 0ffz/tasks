package me.dvyy.tasks

import io.ktor.server.application.*
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.database.createDataSource
import me.dvyy.tasks.database.createDatabase
import me.dvyy.tasks.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    val database = Database.connect(
//        url = environment.config.property("database.url").getString(),
//    )
    val database = createDatabase(createDataSource(environment.config.property("database.url").getString()))
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
