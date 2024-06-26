package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.network.Changelist
import me.dvyy.tasks.routes.login

fun Application.configureRouting(
    server: ServerDataSource,
    jwtConfig: JWTConfig,
    ldapConfig: LDAPConfig,
) {
    routing {
        login(ldapConfig, jwtConfig, server)
        authenticate {
            get("/auth/check") {
                call.respond(HttpStatusCode.OK)
            }

            put("/sync") {
                val changelist = call.receive<Changelist>()
                val session = call.principal<UserSession>() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                call.respond<Changelist>(server.resolveMessages(changelist, session))
            }
        }
    }
}
