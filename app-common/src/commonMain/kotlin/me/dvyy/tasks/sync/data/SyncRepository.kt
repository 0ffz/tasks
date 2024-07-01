package me.dvyy.tasks.sync.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.dvyy.tasks.model.network.Changelist
import me.dvyy.tasks.tasks.data.SyncAPI
import me.dvyy.tasks.utils.AppDispatchers

private const val KEY_LAST_EDIT = "app-last-edit"

class SyncRepository(
    private val syncApi: SyncAPI,
    private val settings: Settings,
    private val messages: MessagesDataSource,
) {
    private val _lastAppSync = MutableStateFlow(settings.decodeValueOrNull(Instant.serializer(), KEY_LAST_EDIT))
    val lastAppSync = _lastAppSync.asStateFlow()

    suspend fun observeLastUpdated() = messages.observeLastUpdated()

    suspend fun sync(lastSynced: Instant? = lastAppSync.value) = withContext(AppDispatchers.db) {
        val now = Clock.System.now()
//        val lastSync = lastAppSync.value ?: Instant.DISTANT_PAST
        val changes = Changelist(
            lastSynced = lastSynced,
            upTo = now,
            messages = messages.getChanges(now)
        )
        val received = withContext(Dispatchers.Default) { syncApi.sync(changes) }
        messages.applyMessages(received.messages)
        messages.clear(now)
        updateSyncTime(now)
    }

    suspend fun fullSync() = withContext(AppDispatchers.db) {
        val now = Clock.System.now()
        messages.createMessagesForAllEntities(now)
        sync()
    }

    private fun updateSyncTime(time: Instant) {
        _lastAppSync.value = time
        settings.encodeValue(Instant.serializer(), KEY_LAST_EDIT, time)
    }
}
