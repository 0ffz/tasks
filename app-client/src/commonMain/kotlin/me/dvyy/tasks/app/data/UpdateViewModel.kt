package me.dvyy.tasks.app.data

import androidx.lifecycle.ViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.dvyy.tasks.BuildKonfig
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.core.ui.PlatformSpecifics

class UpdateViewModel(
    val http: AppHTTP,
) : ViewModel() {
    private val apiUrl = "https://api.github.com/repos/0ffz/tasks/releases"
    private val _latestVersion = MutableStateFlow<String?>(null)
    private val _latestVersionUrl = MutableStateFlow<String?>(null)
    val latestVersion = _latestVersion.asStateFlow()
    val updateUrl = _latestVersionUrl.asStateFlow()
    val currentVersion get() = BuildKonfig.version

    suspend fun fetchUpdates() {
        _latestVersion.update { "Checking..." }
        try {
            val latest = getLatestAppVersion(includePrerelease = true)
            _latestVersion.update { latest }
            if (latest != currentVersion) {
                val downloadUrl = getAppDownloadURL(latest)
                _latestVersionUrl.update { downloadUrl }
            }
        } catch (e: Exception) {
            _latestVersionUrl.update { null }
            _latestVersion.update { "Error getting version" }
            e.printStackTrace()
        }
    }

    /**
     * Gets latest app version as a string
     */
    suspend fun getLatestAppVersion(includePrerelease: Boolean): String {
        val releases = http.client.get(apiUrl).body<JsonArray>()
        val version = releases
            .map { it.jsonObject }
            .first { includePrerelease || it["prerelease"]?.jsonPrimitive?.content == "false" }["tag_name"]!!.jsonPrimitive.content
        return version
    }

    /**
     * Gets release with extension matching the current operating system from a release [version]
     */
    suspend fun getAppDownloadURL(version: String): String? {
        val os = PlatformSpecifics.currentOS
        val extension = os.extension

        val release = http.client.get("$apiUrl/tags/$version").body<JsonElement>().jsonObject
        val assets = release["assets"]?.jsonArray ?: return null

        return assets
            .map { it.jsonObject }
            .firstOrNull { it["name"]?.jsonPrimitive?.content?.endsWith(".$extension") == true }
            ?.get("browser_download_url")?.jsonPrimitive?.content
    }
}