package me.dvyy.tasks.auth.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.ktor.client.plugins.auth.providers.*
import me.dvyy.tasks.sync.data.SyncConfig

private const val KEY_USERNAME = "app-username"
private const val KEY_PASSWORD = "app-password"
private const val KEY_SERVER_URL = "app-server-url"

class CredentialsDataSource(
    private val settings: Settings = Settings(),
) {
    fun readConfig(): SyncConfig? {
        val username: String = settings[KEY_USERNAME] ?: return null
        val password: String = settings[KEY_PASSWORD] ?: return null
        val url: String = settings[KEY_SERVER_URL] ?: return null
        return SyncConfig(url, DigestAuthCredentials(username, password))
    }

    fun writeConfig(config: SyncConfig) {
        settings[KEY_SERVER_URL] = config.url
        settings[KEY_USERNAME] = config.auth.username
        settings[KEY_PASSWORD] = config.auth.password
    }

    fun forgetCredentials() {
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_PASSWORD)
    }
}
