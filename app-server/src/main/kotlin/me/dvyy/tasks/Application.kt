package me.dvyy.tasks

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.SyncServer
import me.dvyy.syncengine.schema.asServerSchema
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database(environment.config.property("database.path").getString())
    val schema = AppSchema.asServerSchema(database)
    val userRepository = UserRepository(database)
    runBlocking {
        userRepository.initialize()
        schema.initialize()
    }
    val syncServer = SyncServer(database, schema)
    val jwtConfig = JWTConfig(environment)
    val ldapConfig = LDAPConfig(environment)

    configureSecurity(jwtConfig)
    configureCORS()
    configureSerialization()
    configureRouting(
        userRepository,
        syncServer,
        jwtConfig,
        ldapConfig
    )
}
