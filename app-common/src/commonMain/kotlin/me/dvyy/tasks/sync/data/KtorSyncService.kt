package me.dvyy.tasks.sync.data

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.auth.data.AppHTTP

class KtorSyncService(
    private val http: AppHTTP,
) : SyncService {
    override suspend fun sync(request: SyncRequest): SyncResult {
        return http.client.post("/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<SyncResult>() //TODO stream back last acknowledged id & row changes
    }
}
