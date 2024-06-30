package me.dvyy.tasks.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.dvyy.tasks.config.JWTConfig

fun Application.configureSecurity(
    jwtConfig: JWTConfig,
) = install(Authentication) {
    jwt {
        realm = jwtConfig.myRealm
        verifier(
            JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.issuer)
                .build()
        )
        validate { credential ->
            val name = credential.payload.getClaim("username").asString() ?: return@validate null
            val userId = credential.payload.getClaim("userId").asString().toIntOrNull() ?: return@validate null
            UserSession(name, userId)
        }
        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}

data class UserSession(
    val username: String,
    val userId: Int,
) : Principal
