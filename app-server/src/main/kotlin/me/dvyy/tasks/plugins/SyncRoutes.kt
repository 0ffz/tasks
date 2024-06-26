package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.dvyy.tasks.model.network.Changelist
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val database = Database.connect(
        url = environment.config.property("database.url").getString(),
    )
    val sync = MessageSync(database)
    routing {
        authenticate {
            get("/test") {
                call.respond(HttpStatusCode.OK, "Hello, ${call.principal<UserSession>()}!")
            }

            put("/sync") {
                val changelist = call.receive<Changelist>()
                call.respond<Changelist>(sync.resolveMessages(changelist))
            }
        }
    }
}
