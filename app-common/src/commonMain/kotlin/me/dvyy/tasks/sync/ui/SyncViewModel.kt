package me.dvyy.tasks.sync.ui

import SyncState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

class SyncViewModel(
//    private val syncRepo: SyncRepository,
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

//            syncRepo.observeLastUpdated()
//                .mapToOneOrNull(Dispatchers.Default)
//                .filter { it != null }
//                .debounce(3.seconds)
//                .collectLatest {
//                    println(it)
//                    trySync()
//                }
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
//        syncRepo.sync()
    }

    fun fullSync() = queueSync {
//        syncRepo.fullSync()
    }

    fun forcePull() = queueSync {
//        syncRepo.sync(lastSynced = null)
    }
}
