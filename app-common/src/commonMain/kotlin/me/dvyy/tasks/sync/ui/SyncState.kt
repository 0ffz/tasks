package me.dvyy.tasks.sync.ui

sealed interface SyncState {
    data object InProgress : SyncState
    data object UnSynced : SyncState
    data object Success : SyncState
    data object Error : SyncState
}