package me.dvyy.tasks.auth.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import me.dvyy.tasks.tasks.data.LoginInfo
import me.dvyy.tasks.tasks.data.SyncConfig

private const val KEY_USERNAME = "app-username"
private const val KEY_PASSWORD = "app-password"
private const val KEY_TOKEN = "app-token"
private const val KEY_SERVER_URL = "app-server-url"

class CredentialsDataSource(
    private val settings: Settings,
) {
    fun readConfig(login: suspend (username: String, password: String) -> String?): SyncConfig? {
        val url: String = settings[KEY_SERVER_URL] ?: return null
        return SyncConfig(
            url,
            loadToken = { settings[KEY_TOKEN] },
            refreshToken = {
                val username = settings.getStringOrNull(KEY_USERNAME) ?: return@SyncConfig null
                val password = settings.getStringOrNull(KEY_PASSWORD) ?: return@SyncConfig null
                login(username, password)
            }
        )
    }

    fun getLoginInfo(): LoginInfo? {
        return LoginInfo(
            settings[KEY_SERVER_URL] ?: return null,
            settings[KEY_USERNAME] ?: return null,
        )
    }

    fun saveCredentials(
        url: String,
        username: String,
        password: String,
        token: String?,
    ) {
        settings[KEY_SERVER_URL] = url
        settings[KEY_USERNAME] = username
        settings[KEY_PASSWORD] = password
        settings[KEY_TOKEN] = token
    }

    fun forgetCredentials() {
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_PASSWORD)
        settings.remove(KEY_TOKEN)
    }
}
