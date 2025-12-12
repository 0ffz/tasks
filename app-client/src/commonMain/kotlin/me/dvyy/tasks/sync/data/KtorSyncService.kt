package me.dvyy.tasks.sync.data

import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.syncengine.sync.SyncRequest
import me.dvyy.syncengine.sync.SyncResult
import me.dvyy.syncengine.sync.SyncService
import me.dvyy.tasks.auth.data.AppHTTP
import kotlin.uuid.Uuid

class KtorSyncService(
    private val http: AppHTTP,
) : SyncService {
    override suspend fun sync(uuid: Uuid, initialRequest: SyncRequest, request: Flow<SyncRequest>): Flow<SyncResult> =
        if (http.config == null) emptyFlow() else
        flow {
            println(http.config)
            http.client.webSocket("/sync") {
                sendSerialized(initialRequest)
                launch {
                    request.collect { sendSerialized<SyncRequest>(it) }
                }
                emitAll(incoming.consumeAsFlow().map {
                    converter!!.deserialize<SyncResult>(it)
                })
            }
        }
}
