package me.dvyy.tasks

import co.touchlab.kermit.Logger
import com.sun.security.auth.module.UnixSystem
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import me.dvyy.sqlite.Database
import me.dvyy.syncengine.reducers.Reducers
import me.dvyy.syncengine.schema.Schema
import me.dvyy.syncengine.server.schema.SyncServer
import me.dvyy.syncengine.server.schema.WorkspaceRepository
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.database.commonSyncModule
import me.dvyy.tasks.plugins.*
import me.dvyy.tasks.server.database.ServerQueries
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Logger.setLogWriters(listOf(LogbackLogWriter))
    val cwd = Path(".")
    if (!cwd.toFile().canWrite()) {
        val system = UnixSystem()
        val user = system.uid
        val group = system.gid
        Logger.e { "No write permissions to /data, running as $user:$group" }
        error("Failed to start")
    }
    install(Koin) {
        modules(commonSyncModule(), module {
            single<Logger> { Logger }
            single {
                val dbPath = environment.config.property("database.path").getString()
                val absolute = Path(dbPath).absolutePathString()
                Logger.i { "Opening database at $absolute" }
                Database(absolute)
            }
            single {
                WorkspaceRepository(
                    get(),
                    workspacesFolder = Path(environment.config.property("database.workspacesFolder").getString()),
                    logger = koin.get<Logger>(),
                    schema = koin.get<Schema>(),
                    reducers = koin.get<Reducers>(),
                    stopTimeoutMillis = environment.config.property("database.stopTimeoutMillis").getString().toLong()
                )
            }
            single { UserRepository(get(), ServerQueries()) }
            singleOf(::SyncServer)
        })
        createEagerInstances()
    }
    val userRepository = get<UserRepository>()
    val syncServer = get<SyncServer>()

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
