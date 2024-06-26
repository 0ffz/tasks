package me.dvyy.tasks.tasks.data

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.model.network.Changelist

class LoginInfo(
    val url: String,
    val username: String,
)

class SyncConfig(
    val url: String,
    val loadToken: suspend () -> String?,
    val refreshToken: suspend () -> String?,
)

class SyncAPI(
    private val http: AppHTTP,
) {
    suspend fun sync(
        changelist: Changelist,
    ) = http.client.put("/sync") {
        contentType(ContentType.Application.Json)
        setBody(changelist)
    }.body<Changelist>()
}
