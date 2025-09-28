package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.dvyy.syncengine.SyncServer
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.routes.login

fun Application.configureRouting(
    userRepository: UserRepository,
    syncServer: SyncServer,
    jwtConfig: JWTConfig,
    ldapConfig: LDAPConfig,
) {
    routing {
        login(userRepository, ldapConfig, jwtConfig)
        authenticate {
            get("/auth/check") {
                call.respond(HttpStatusCode.OK)
            }
            put("/sync") {
                val changelist = call.receive<SyncRequest>()
                val session = call.principal<UserSession>() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val result = syncServer.sync(changelist, session.identity)
                call.respond<SyncResult>(result)
            }
        }
    }
}
