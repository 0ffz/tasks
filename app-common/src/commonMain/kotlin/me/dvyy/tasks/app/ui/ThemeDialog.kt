package me.dvyy.tasks.app.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.di.koinViewModel

@Composable
fun ThemeDialog(
    dialogs: DialogViewModel = koinViewModel(),
    prefs: PreferencesViewModel = koinViewModel(),
) {
    val prefsTheme by prefs.theme.collectAsState()
    var theme by remember { mutableStateOf(prefsTheme) }
    AlertDialog(
        onDismissRequest = { dialogs.dismiss() },
        title = { Text("Theme") },
        text = {
            OutlinedTextField(
                theme,
                onValueChange = { theme = it },
//                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                minLines = 8,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                prefs.theme.update { theme }
                dialogs.dismiss()
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = { dialogs.dismiss() }) { Text("Dismiss") }
        }
    )
}
