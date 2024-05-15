package me.dvyy.tasks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

fun AuthenticationConfig.custom(
    name: String? = null,
) {
    val provider = HeaderAuthenticationProvider(HeaderAuthenticationProvider.Configuration(name))
    register(provider)
}

class HeaderAuthenticationProvider(config: Configuration) : AuthenticationProvider(config) {
    //Configuration
    class Configuration internal constructor(name: String?) : Config(name) {
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
//        val cause = when {
//            credentials == null ->
//            principal == null -> AuthenticationFailedCause.InvalidCredentials
//            else -> null
//        }
        val emailHeader = context.call.request.headers["X-Auth-Request-Email"]
        val usernameHeader = context.call.request.headers["X-Auth-Request-User"]
        val cause = AuthenticationFailedCause.NoCredentials
        if (emailHeader == null) context.challenge("CustomAuth", cause) { challenge, call ->
            call.respond(HttpStatusCode.Unauthorized)
//            call.respond(UnauthorizedResponse(HttpAuthHeader.basicAuthChallenge(realm, charset)))
            challenge.complete()
        } else
            context.principal(name, UserSession(emailHeader, "admin"))

    }
}

fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

val myRealm = "Access to the '/' path"
val userTable: Map<String, ByteArray> = mapOf(
    "admin" to getMd5Digest("admin:$myRealm:password")
)

fun Application.configureSecurity() {
    install(Authentication) {
//        custom()
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
