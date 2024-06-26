package me.dvyy.tasks.auth.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.dvyy.tasks.tasks.data.LoginInfo

class AuthRepository(
    private val http: AppHTTP,
    private val source: CredentialsDataSource,
    private val authAPI: AuthAPI,
    ioDispatcher: CoroutineDispatcher,
) {
    private val loginDispatcher = ioDispatcher.limitedParallelism(1)

    suspend fun configureUsingSavedCredentials(): LoginInfo? = withContext(loginDispatcher) {
        val config = source.readConfig { username, password ->
            (authAPI.login(username, password) as? AuthResult.Success)?.token
        } ?: return@withContext null
        http.configure(config)
        source.getLoginInfo()
    }

    suspend fun login(
        url: String,
        username: String,
        password: String,
    ): AuthResult = withContext(loginDispatcher) {
        val result = authAPI.login(username, password, serverUrl = url)
        when (result) {
            is AuthResult.Success -> {
                source.saveCredentials(url, username, password, result.token)
                configureUsingSavedCredentials()
            }

            else -> {}
        }
        result
    }

    suspend fun logout() = withContext(loginDispatcher) {
        source.forgetCredentials()
    }
}
