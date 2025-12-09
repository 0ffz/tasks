package me.dvyy.tasks

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.MutatorApplier
import me.dvyy.syncengine.SyncServer
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.mutators.Mutator
import me.dvyy.tasks.plugins.*
import me.dvyy.tasks.server.database.ServerDatabase

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database(environment.config.property("database.path").getString())
    val schema = AppSchema
    val userRepository = UserRepository(database, ServerDatabase())
    val applier = MutatorApplier(AppDAO(database), Mutator.serializer())
    val syncServer = SyncServer(database, schema, applier)
    runBlocking {
        userRepository.initialize()
        syncServer.initialize()
    }
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
