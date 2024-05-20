package me.dvyy.tasks.ui.elements.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.state.AppDialog
import me.dvyy.tasks.state.DialogState
import me.dvyy.tasks.ui.elements.sync.AuthDialog
import org.koin.compose.koinInject

@Composable
fun AppDialogs(app: DialogState = koinInject()) {
    val dialog by app.active.collectAsState()

    when (dialog) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
    }
}
