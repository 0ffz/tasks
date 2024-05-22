package me.dvyy.tasks.app.ui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.app.ui.AppDialog
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.auth.ui.AuthDialog
import me.dvyy.tasks.di.koinViewModel

@Composable
fun AppDialogs(app: DialogViewModel = koinViewModel()) {
    val dialog by app.active.collectAsState()

    when (dialog) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
    }
}
