package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.syncengine.client.sync.SyncClient
import java.net.ConnectException
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class SyncViewModel(
    private val syncClient: SyncClient,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    init {
        viewModelScope.launch {
            fun trySync() = runCatching {
                sync()
            }.onFailure {
                if (it is ConnectException) println(it.message)
                else it.printStackTrace()
            }

            trySync()
            syncClient.changesMade.debounce(3.seconds).collectLatest {
                println("Changes made")
                trySync()
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
