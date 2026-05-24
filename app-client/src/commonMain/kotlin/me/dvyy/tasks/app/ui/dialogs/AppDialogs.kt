package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel


//@Composable
//fun AppDialogs(app: DialogViewModel = koinViewModel()) {
//    val dialogState by app.active.collectAsState()
//
//    when (val dialog = dialogState) {
//        null -> {}
//        AppDialog.Auth -> AuthDialog()
//        is AppDialog.ConfirmDeleteProject -> ConfirmDeleteProjectDialog(dialog.key)
//    }
//}


@Composable
fun ConfirmDeleteProjectDialog(
    key: ListId,
    onDismiss: () -> Unit,
    tasks: TasksViewModel = koinViewModel(),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(AppIcons.Delete, contentDescription = "Delete") },
        title = { Text("Delete project") },
        text = { Text("This will delete the projects and any tasks in it. Are you sure?") },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                tasks.deleteProject(key.uuid)
            }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        },
        modifier = Modifier.imePadding()
    )
}
