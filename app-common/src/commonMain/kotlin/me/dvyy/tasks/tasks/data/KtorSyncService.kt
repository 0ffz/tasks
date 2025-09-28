package me.dvyy.tasks.tasks.data

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.auth.data.AppHTTP

class LoginInfo(
    val url: String,
    val username: String,
)

class SyncConfig(
    val url: String,
    val loadToken: suspend () -> String?,
    val refreshToken: suspend () -> String?,
)

class KtorSyncService(
    private val http: AppHTTP,
) : SyncService {
    override suspend fun sync(request: SyncRequest): SyncResult {
        return http.client.post("/sync") {
            contentType(ContentType.Application.ProtoBuf)
            setBody(request)
        }.body<SyncResult>() //TODO stream back last acknowledged id & row changes
    }
}
