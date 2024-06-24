package me.dvyy.tasks.auth.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import me.dvyy.tasks.auth.data.AppHTTP.AuthResult
import me.dvyy.tasks.tasks.data.SyncConfig

class AuthRepository(
    private val http: AppHTTP,
    private val source: CredentialsDataSource,
    ioDispatcher: CoroutineDispatcher,
) {
    private val credentials = MutableStateFlow(source.readConfig()?.auth)
    private val serverUrl = MutableStateFlow(source.readConfig()?.url)
    private val loginDispatcher = ioDispatcher.limitedParallelism(1)

    suspend fun loadSavedConfig(): SyncConfig? = withContext(loginDispatcher) {
        val config = source.readConfig() ?: return@withContext null
        http.configure(config)
        credentials.value = config.auth
        serverUrl.value = config.url
        config
    }

    suspend fun login(config: SyncConfig): AuthResult = withContext(loginDispatcher) {
        val result = http.checkAuth(config)
        if (result != AuthResult.SUCCESS) return@withContext result

        http.configure(config)
        source.writeConfig(config)
        credentials.value = config.auth
        serverUrl.value = config.url
        result
    }

    suspend fun logout() = withContext(loginDispatcher) {
        source.forgetCredentials()
        credentials.value = null
    }
}
