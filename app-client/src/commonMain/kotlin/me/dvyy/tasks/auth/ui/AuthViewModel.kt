package me.dvyy.tasks.auth.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.dvyy.tasks.auth.data.AuthRepository
import me.dvyy.tasks.auth.data.AuthResult

class AuthViewModel(
    private val userRepo: AuthRepository,
) : ViewModel() {
    val loginState get() = _loginState
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    init {
        viewModelScope.launch {
            when (val config = userRepo.configureUsingSavedCredentials()) {
                null -> loginState.value = LoginState.NoLogin
                else -> loginState.value = LoginState.Success(config.username, config.url)
            }
        }
    }

    suspend fun login(url: String, username: String, password: String): AuthResult {
        val result = userRepo.login(url, username, password)
        when (result) {
            is AuthResult.Success -> _loginState.value = LoginState.Success(username, url)
            AuthResult.ConnectionError -> _loginState.value = LoginState.Error.Connection
            AuthResult.InvalidCredentials -> _loginState.value = LoginState.Error.InvalidCredentials
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
