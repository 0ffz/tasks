package me.dvyy.tasks

import co.touchlab.kermit.Logger
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.server.schema.ServerActionProcessor
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.commonSyncModule
import me.dvyy.tasks.plugins.*
import me.dvyy.tasks.server.database.ServerDatabase
import org.koin.dsl.koinApplication

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database(environment.config.property("database.path").getString())
    val schema = AppSchema
    val koin = koinApplication {
        modules(commonSyncModule())
    }.koin
    val reducers = koin.get<Reducers>()
    val userRepository = UserRepository(database, ServerDatabase())
    val applier = ServerActionProcessor(Logger, reducers)
    val syncServer = SyncServer(Logger, database, schema, applier)
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
