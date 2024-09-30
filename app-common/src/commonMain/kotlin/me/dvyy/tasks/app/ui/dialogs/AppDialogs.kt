package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.auth.ui.AuthDialog
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed interface AppDialog {
    data object Auth : AppDialog
    data class ConfirmDeleteProject(val key: ListId) : AppDialog
}

@Composable
fun AppDialogs(app: DialogViewModel = koinViewModel()) {
    val dialogState by app.active.collectAsState()

    when (val dialog = dialogState) {
        null -> {}
        AppDialog.Auth -> AuthDialog()
        is AppDialog.ConfirmDeleteProject -> ConfirmDeleteProjectDialog(dialog.key)
    }
}


@Composable
fun ConfirmDeleteProjectDialog(
    key: ListId,
    dialogs: DialogViewModel = koinViewModel(),
    tasks: TasksViewModel = koinViewModel(),
) {
    AlertDialog(
        onDismissRequest = { dialogs.dismiss() },
        icon = { Icon(AppIcons.Delete, contentDescription = "Delete") },
        title = { Text("Delete project") },
        text = { Text("This will delete the projects and any tasks in it. Are you sure?") },
        confirmButton = {
            TextButton(onClick = {
                dialogs.dismiss()
                tasks.deleteProject(key)
            }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = { dialogs.dismiss() }) { Text("Dismiss") }
        },
        modifier = Modifier.imePadding()
    )
}
