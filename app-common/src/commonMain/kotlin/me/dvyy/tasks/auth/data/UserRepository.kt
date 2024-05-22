package me.dvyy.tasks.auth.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import me.dvyy.tasks.sync.data.SyncClient
import me.dvyy.tasks.sync.data.SyncClient.AuthResult.SUCCESS
import me.dvyy.tasks.sync.data.SyncConfig

class UserRepository(
    private val sync: SyncClient,
    private val source: CredentialsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val credentials = MutableStateFlow(source.readConfig()?.auth)
    private val serverUrl = MutableStateFlow(source.readConfig()?.url)
    private val loginDispatcher = ioDispatcher.limitedParallelism(1)

    suspend fun loadSavedConfig(): SyncConfig? = withContext(loginDispatcher) {
        val config = source.readConfig() ?: return@withContext null
        sync.configure(config)
        credentials.value = config.auth
        serverUrl.value = config.url
        config
    }

    suspend fun login(config: SyncConfig): SyncClient.AuthResult = withContext(loginDispatcher) {
        val result = sync.checkAuth(config)
        if (result != SUCCESS) return@withContext result

        sync.configure(config)
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
