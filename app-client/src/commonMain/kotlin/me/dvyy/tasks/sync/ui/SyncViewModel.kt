package me.dvyy.tasks.sync.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.client.sync.SyncStatus

@OptIn(FlowPreview::class)
class SyncViewModel(
    private val syncClient: SyncClient,
) : ViewModel() {
    val queuedActionCount = syncClient.changesMade
        .map { syncClient.getQueuedActionCount() }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)
    private val syncEnabled = MutableStateFlow(true)
    val syncState = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        val status by syncClient.status.collectAsState()
        val enabled by syncEnabled.collectAsState()
        return@launchMolecule when {
            !enabled -> SyncUiState.Disabled
            status is SyncStatus.Connected -> SyncUiState.Connected
            else -> SyncUiState.Connecting
        }
    }

    init {
        viewModelScope.launch {
            combine(syncClient.status, syncEnabled) { status, enabled -> status to enabled }
                .collectLatest { (status, enabled) ->
                    Logger.v { "Sync status: $status" }
                    if (status !is SyncStatus.Connected && enabled) {
                        syncClient.startSyncJob(context = Dispatchers.IO).join()
                    }
                }
        }
        viewModelScope.launch {
            syncEnabled.collectLatest { if (!it) syncClient.stopSyncJob() }
        }
    }

    fun enableSync() {
        syncEnabled.update { true }
    }

    fun toggleSync() {
        syncEnabled.update { !it }
    }

    fun disableSync() {
        syncEnabled.update { false }
    }

    private suspend fun reconnectWithBackoff() {
        var currentDelay = 1000L
        val maxDelay = 60000L
        while (true) {
            try {
                syncClient.startSyncJob(context = Dispatchers.IO).join()
                Logger.v { "Reconnecting to server with delay $currentDelay ms..." }
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelay)
                Logger.v { "Failed to connect to server, retrying in $currentDelay ms..." }
                throw e
            }
        }
    }
}
