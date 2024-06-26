package me.dvyy.tasks.auth.data

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.data.SyncConfig

class AppHTTP(
    private val ioDispatcher: CoroutineDispatcher,
) {
    val baseClient = HttpClient {
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
    val config: SyncConfig? get() = _config
    private var _client = baseClient
    private var _config: SyncConfig? = null

    fun HttpClient.configure(config: SyncConfig): HttpClient {
        return config {
            defaultRequest {
                url(config.url)
            }
            install(Auth) {
                bearer {
                    realm = "Access to sync api"
                    loadTokens {
                        val token = config.loadToken() ?: return@loadTokens null
                        BearerTokens(accessToken = token, refreshToken = token)
                    }
                    refreshTokens {
                        val token = config.refreshToken() ?: return@refreshTokens null
                        BearerTokens(accessToken = token, refreshToken = token)
                    }
                }
            }
        }
    }

    fun configure(config: SyncConfig) {
        _client = baseClient.configure(config)
        _config = config
    }
}
