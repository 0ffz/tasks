package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.tasks.ui.SyncState
import java.net.ConnectException
import kotlin.time.Duration.Companion.seconds

class SyncViewModel(
    private val syncRepo: SyncRepository,
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

            //TODO reimplement sync
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
        syncRepo.sync()
    }

    fun fullSync() = queueSync {
        syncRepo.fullSync()
    }

    fun forcePull() = queueSync {
        syncRepo.sync(lastSynced = null)
    }
}
