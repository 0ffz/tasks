package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.auth.ui.AuthDialog
import org.koin.compose.viewmodel.koinViewModel

sealed interface AppDialog {
    data object Auth : AppDialog
}

@Composable
fun AppDialogs(app: DialogViewModel = koinViewModel()) {
    val dialog by app.active.collectAsState()

    when (dialog) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
    }
}
