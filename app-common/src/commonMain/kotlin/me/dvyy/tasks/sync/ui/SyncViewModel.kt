package me.dvyy.tasks.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.dvyy.tasks.sync.data.SyncRepository
import me.dvyy.tasks.tasks.ui.SyncState

class SyncViewModel(
    private val syncRepo: SyncRepository,
) : ViewModel() {
    val syncState: StateFlow<SyncState> get() = _syncState
    private val _syncState = MutableStateFlow<SyncState>(SyncState.UnSynced)

    fun queueSync() = viewModelScope.launch {
        if (syncState.value == SyncState.InProgress) return@launch
        _syncState.value = SyncState.InProgress
        runCatching {
            syncRepo.sync()
        }.onFailure {
            _syncState.value = SyncState.Error
            it.printStackTrace()
        }.onSuccess {
            _syncState.value = SyncState.Success
        }
    }
}
