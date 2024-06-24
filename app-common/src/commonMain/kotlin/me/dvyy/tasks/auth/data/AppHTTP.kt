package me.dvyy.tasks.auth.data

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.data.SyncConfig

class AppHTTP(
    private val ioDispatcher: CoroutineDispatcher,
) {
//    val inProgress = MutableStateFlow(false)
//    val isError = MutableStateFlow(false)
//    val diffRemoved = mutableSetOf<Uuid>()

    private val baseClient = HttpClient {
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule = AppFormats.networkModule
            })
        }
    }

    val client: HttpClient get() = _client
    private var _client = baseClient

    fun HttpClient.configure(config: SyncConfig): HttpClient {
        return config {
            defaultRequest {
                url(config.url)
            }
            install(Auth) {
                digest {
                    credentials { config.auth }
                }
            }
        }
    }

    fun configure(config: SyncConfig) {
        _client = baseClient.configure(config)
    }

    enum class AuthResult {
        SUCCESS, INVALID_CREDENTIALS, CONNECTION_ERROR
    }

    suspend fun checkAuth(config: SyncConfig): AuthResult = withContext(ioDispatcher) {
        val result = runCatching { _client.configure(config).get("auth/check").status == HttpStatusCode.OK }
            .getOrElse { return@withContext AuthResult.CONNECTION_ERROR }
        if (result) AuthResult.SUCCESS else AuthResult.INVALID_CREDENTIALS
    }
}
