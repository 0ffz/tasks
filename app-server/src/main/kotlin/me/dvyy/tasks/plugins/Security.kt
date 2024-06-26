package me.dvyy.tasks.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.benasher44.uuid.Uuid
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
            val uuid = Uuid.fromString(credential.payload.getClaim("uuid").asString()) ?: return@validate null
            UserSession(name, uuid)
        }
        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}

data class UserSession(
    val username: String,
    val uuid: Uuid,
) : Principal
