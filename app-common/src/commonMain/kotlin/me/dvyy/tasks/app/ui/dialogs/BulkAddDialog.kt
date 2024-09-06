package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel

@Composable
fun BulkAddDialog(
    dialogs: DialogViewModel = koinViewModel(),
    tasks: TasksViewModel = koinViewModel(),
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { dialogs.dismiss() },
        title = { Text("Bulk add") },
        text = {
            Column {
                Text("Enter tasks, one per line, use ^ for date, ! for highlight, : for text, ex\n !1 ^2024-12-31 :Do something important!")
                Spacer(Modifier.height(2.dp))
                OutlinedTextField(
                    text,
                    onValueChange = { text = it },
                    minLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tasks.bulkAdd(text.lines())
                dialogs.dismiss()
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = { dialogs.dismiss() }) { Text("Dismiss") }
        },
        modifier = Modifier.imePadding()
    )
}
