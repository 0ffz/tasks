package me.dvyy.tasks.app.ui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.app.ui.AppDialog
import me.dvyy.tasks.app.ui.DialogState
import me.dvyy.tasks.auth.ui.AuthDialog
import org.koin.compose.koinInject

@Composable
fun AppDialogs(app: DialogState = koinInject()) {
    val dialog by app.active.collectAsState()

    when (dialog) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
    }
}
