package me.dvyy.tasks.app.data

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.dvyy.tasks.auth.data.AppHTTP

class UpdateRepository(
    val http: AppHTTP,
) {
    private val apiUrl = "https://api.github.com/repos/0ffz/tasks/releases"
    private var cachedVersion: String? = null

    suspend fun getLatestAppVersion(includePrerelease: Boolean): String {
        cachedVersion?.let { return it }
        val releases = http.client.get(apiUrl).body<JsonArray>()
        val version = releases
            .map { it.jsonObject }
            .first { includePrerelease || it["prerelease"]?.jsonPrimitive?.content == "false" }["tag_name"]!!.jsonPrimitive.content
        cachedVersion = version
        return version
    }
}