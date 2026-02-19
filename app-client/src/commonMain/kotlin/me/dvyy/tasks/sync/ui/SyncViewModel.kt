package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.client.sync.SyncStatus

@OptIn(FlowPreview::class)
class SyncViewModel(
    private val syncClient: SyncClient,
) : ViewModel() {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)
    val syncState = _syncState.asStateFlow()
    val queuedActionCount = syncClient.changesMade
        .map { syncClient.getQueuedActionCount() }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    init {
        viewModelScope.launch {
            syncClient.status.collectLatest { status ->
                if (status !is SyncStatus.Connected) {
                    reconnectWithBackoff()
                }
            }
        }
    }

    private suspend fun reconnectWithBackoff() {
        var currentDelay = 1000L
        val maxDelay = 60000L
        while (true) {
            try {
                syncClient.startSyncJob(context = Dispatchers.IO)
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelay)
                Logger.v { "Failed to connect to server, retrying in $currentDelay ms..." }
            }
        }
    }

    private inline fun queueSync(crossinline run: suspend () -> Unit) = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        _syncState.value = SyncState.InProgress
        runCatching {
            run()
        }.onFailure {
            it.printStackTrace()
            _syncState.update { SyncState.Error }
        }.onSuccess {
            _syncState.update { SyncState.Success }
        }
    }
}
