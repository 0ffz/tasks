package me.dvyy.tasks.sync.data

import co.touchlab.kermit.Logger
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
    override suspend fun sync(uuid: Uuid, initialRequest: SyncRequest, request: Flow<SyncRequest>): Flow<SyncResult> {
        return if (http.config == null) emptyFlow() else
            flow {
                val syncRoute = "${http.config!!.websocketUrl}/sync"
                Logger.d { "Establishing sync connection to url: $syncRoute" }
                runCatching {
                    http.client.webSocket(syncRoute) {
                        sendSerialized(initialRequest)
                        launch {
                            request.collect { sendSerialized<SyncRequest>(it) }
                        }
                        emitAll(incoming.consumeAsFlow().map {
                            converter!!.deserialize<SyncResult>(it)
                        })
                    }
                }.onFailure {
                    Logger.e(it) { "Sync websocket errored!" }
                }
            }
    }
}
