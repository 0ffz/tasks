package me.dvyy.tasks.ui.elements.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.state.AppDialog
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.ui.screens.auth.AuthDialog

@Composable
fun AppDialogs() {
    val state = LocalAppState
    val dialog by state.activeDialog.collectAsState()

    when (dialog) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
    }
}
