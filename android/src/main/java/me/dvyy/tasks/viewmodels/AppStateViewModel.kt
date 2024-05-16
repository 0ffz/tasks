package me.dvyy.tasks.viewmodels

import androidx.lifecycle.ViewModel
import me.dvyy.tasks.state.AppState

class AppStateViewModel : ViewModel() {
    val appState = AppState()
}
