package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AppScreen {

    data object Settings : AppScreen
}
class DialogViewModel : ViewModel() {
    private val _active = MutableStateFlow<AppDialog?>(null)
    val active = _active.asStateFlow()
    val screen = MutableStateFlow<AppScreen?>(null)

    fun dismiss() = _active.tryEmit(null)
    fun show(dialog: AppDialog) = _active.tryEmit(dialog)
    fun showScreen(show: AppScreen) = screen.tryEmit(show)
}
