package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

val myRealm = "Access to the '/' path"
val userTable: Map<String, ByteArray> = mapOf(
    "admin" to getMd5Digest("admin:$myRealm:password")
)

fun Application.configureSecurity() {
    install(Authentication) {
        digest {
            realm = myRealm
            digestProvider { userName, realm ->
                userTable[userName]
            }
            validate { credentials ->
                if (credentials.userName.isNotEmpty()) {
                    UserSession(credentials.userName, credentials.userName)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate {
            get("/auth/check") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

data class UserSession(val email: String, val username: String) : Principal
