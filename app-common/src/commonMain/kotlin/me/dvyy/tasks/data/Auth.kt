package me.dvyy.tasks.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.ktor.client.plugins.auth.providers.*
import kotlinx.coroutines.flow.MutableStateFlow

private const val KEY_USERNAME = "app-username"
private const val KEY_PASSWORD = "app-password"


class Auth {
    val settings = Settings()
    val username = MutableStateFlow(settings.get<String>(KEY_USERNAME))

    fun setAuth(username: String, password: String) {
        settings[KEY_USERNAME] = username
        settings[KEY_PASSWORD] = password
        this.username.value = username
    }

    fun getAuth(): DigestAuthCredentials? {
        val username = settings.get<String>(KEY_USERNAME) ?: return null
        val password = settings.get<String>(KEY_PASSWORD) ?: return null
        return DigestAuthCredentials(username, password)
    }

    fun logout() {
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_PASSWORD)
        this.username.value = null
    }
}
