package me.dvyy.tasks.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.ldap.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.dvyy.tasks.config.JWTConfig
import me.dvyy.tasks.config.LDAPConfig
import me.dvyy.tasks.model.auth.AuthRequest
import me.dvyy.tasks.plugins.UserRepository
import java.util.*

fun Route.login(
    userRepository: UserRepository,
    ldapConfig: LDAPConfig,
    jwtConfig: JWTConfig,
) = post("/login") {
    val user = call.receive<AuthRequest>()

    val userPrincipal = ldapAuthenticate(
        UserPasswordCredential(user.username, user.password),
        ldapConfig.connection,
        ldapConfig.userDNFormat
    )
        ?: return@post call.respond(HttpStatusCode.Conflict, "Invalid credentials")

    val userId = userRepository.getOrCreateUserId(userPrincipal.name)

    val token = JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.issuer)
        .withClaim("username", user.username)
        .withClaim("userId", userId.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 20))
        .sign(Algorithm.HMAC256(jwtConfig.secret))

    call.respond(hashMapOf("token" to token))
}
