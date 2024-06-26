package me.dvyy.tasks.auth.data

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.dvyy.tasks.model.auth.AuthRequest


sealed interface AuthResult {
    data class Success(val token: String) : AuthResult
    data object InvalidCredentials : AuthResult
    data object ConnectionError : AuthResult
}

class AuthAPI(
    val appHTTP: AppHTTP,
) {
    suspend fun login(
        username: String,
        password: String,
        serverUrl: String? = null,
    ): AuthResult {
        val token = runCatching {
            appHTTP.baseClient.post("${serverUrl ?: appHTTP.config?.url ?: ""}/login") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }.takeIf { it.status == HttpStatusCode.OK }
                ?.body<Map<String, String>>()
                ?.get("token")
                ?: return AuthResult.InvalidCredentials
        }.getOrElse { return AuthResult.ConnectionError }
        return AuthResult.Success(token)
    }
}
