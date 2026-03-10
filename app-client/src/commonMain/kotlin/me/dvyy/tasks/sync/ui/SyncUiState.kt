package me.dvyy.tasks.sync.ui

sealed interface SyncUiState {
    data object Connecting : SyncUiState
    data object Connected : SyncUiState
    data object Error : SyncUiState
    data object Disabled : SyncUiState
}