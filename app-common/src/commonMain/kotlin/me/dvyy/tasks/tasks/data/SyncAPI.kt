package me.dvyy.tasks.tasks.data

import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import me.dvyy.tasks.auth.data.AppHTTP
import me.dvyy.tasks.model.network.Changelist

class SyncConfig(
    val url: String,
    val auth: DigestAuthCredentials,
)

class SyncAPI(
    private val http: AppHTTP,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun sync(
        changelist: Changelist,
    ) = http.client.put("/sync") {
        contentType(ContentType.Application.Json)
        setBody(changelist)
    }.body<Changelist>()
}
