package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.syncengine.client.sync.SyncClient
import java.net.ConnectException

@OptIn(FlowPreview::class)
class SyncViewModel(
    private val syncClient: SyncClient,
) : ViewModel() {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)
    val syncState = _syncState.asStateFlow()

    private var runningSyncJob: Job? = null
    fun startSyncJob() {
        if (runningSyncJob == null) {
            runningSyncJob = viewModelScope.launch {
                syncClient.establishSync()
            }
            _syncState.update { SyncState.Connected }
        }
    }

    fun stopSyncJob() {
        runningSyncJob?.cancel()
        runningSyncJob = null
        _syncState.update { SyncState.Disconnected }
    }
    init {
        viewModelScope.launch {
            fun trySync() = runCatching {
                sync()
            }.onFailure {
                if (it is ConnectException) println(it.message)
                else it.printStackTrace()
            }
        }
        startSyncJob()
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

    fun sync() = queueSync {
        syncClient.sync()
    }

    fun fullSync() = queueSync {
        TODO()
//        syncClient.fullSync()
    }

    fun forcePull() = queueSync {
        TODO()
//        syncClient.sync(lastSynced = null)
    }
}
