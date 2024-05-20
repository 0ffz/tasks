package me.dvyy.tasks.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DialogState {
    val active: StateFlow<AppDialog?> get() = _active
    private val _active = MutableStateFlow<AppDialog?>(null)

    fun dismiss() = _active.tryEmit(null)
    fun show(dialog: AppDialog) = _active.tryEmit(dialog)
}
