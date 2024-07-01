package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.tasks.ui.SyncState

class SyncViewModel(
    private val syncRepo: SyncRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    init {
//        viewModelScope.launch {
//            syncRepo.observeLastUpdated()
//                .mapToOneOrNull(Dispatchers.Default)
//                .debounce(10.seconds)
//                .collect {
//                    queueSync()
//                }
//        }
    }

    private inline fun queueSync(crossinline run: suspend () -> Unit) = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        _syncState.value = SyncState.InProgress
        runCatching {
            run()
        }.onFailure {
            _syncState.update { SyncState.Error }
            it.printStackTrace()
        }.onSuccess {
            _syncState.update { SyncState.Success }
        }
    }

    fun sync() = queueSync {
        syncRepo.sync()
    }

    fun fullSync() = queueSync {
        syncRepo.fullSync()
    }

    fun forcePull() = queueSync {
        syncRepo.sync(lastSynced = null)
    }
}
