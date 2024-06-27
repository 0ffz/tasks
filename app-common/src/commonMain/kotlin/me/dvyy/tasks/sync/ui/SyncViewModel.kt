package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.tasks.ui.SyncState
import kotlin.time.Duration.Companion.seconds

class SyncViewModel(
    private val syncRepo: SyncRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    init {
        viewModelScope.launch {
            syncRepo.observeLastUpdated()
                .debounce(10.seconds)
                .collect {
                    queueSync()
                }
        }
    }

    fun queueSync() = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        _syncState.value = SyncState.InProgress
        runCatching {
            syncRepo.sync()
        }.onFailure {
            _syncState.update { SyncState.Error }
            it.printStackTrace()
        }.onSuccess {
            _syncState.update { SyncState.Success }
        }
    }

    fun fullSync() = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        _syncState.value = SyncState.InProgress
        runCatching {
            syncRepo.fullSync()
        }.onFailure {
            _syncState.update { SyncState.Error }
            it.printStackTrace()
        }.onSuccess {
            _syncState.update { SyncState.Success }
        }
    }
}
