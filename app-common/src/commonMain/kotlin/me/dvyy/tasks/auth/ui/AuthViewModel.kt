package me.dvyy.tasks.auth.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.auth.providers.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.dvyy.tasks.auth.data.UserRepository
import me.dvyy.tasks.sync.data.SyncClient.AuthResult
import me.dvyy.tasks.sync.data.SyncClient.AuthResult.*
import me.dvyy.tasks.sync.data.SyncConfig

class AuthViewModel(
    private val userRepo: UserRepository,
) : ViewModel() {
    val loginState get() = _loginState
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    init {
        viewModelScope.launch {
            when (val config = userRepo.loadSavedConfig()) {
                null -> loginState.value = LoginState.NoLogin
                else -> loginState.value = LoginState.Success(config.auth.username, config.url)
            }
        }
    }

    suspend fun login(url: String, username: String, password: String): AuthResult {
        val result = userRepo.login(SyncConfig(url, DigestAuthCredentials(username, password)))
        when (result) {
            SUCCESS -> _loginState.value = LoginState.Success(username, url)
            CONNECTION_ERROR -> _loginState.value = LoginState.Error.Connection
            INVALID_CREDENTIALS -> _loginState.value = LoginState.Error.InvalidCredentials
        }
        return result
    }

    fun logout() = viewModelScope.launch {
        userRepo.logout()
        _loginState.value = LoginState.NoLogin
    }
}


@Stable
sealed interface LoginState {
    data object NoLogin : LoginState
    data object Loading : LoginState
    sealed interface Error : LoginState {
        data object Connection : Error
        data object InvalidCredentials : Error
    }

    data class Success(
        val username: String,
        val serverURL: String,
    ) : LoginState
}
