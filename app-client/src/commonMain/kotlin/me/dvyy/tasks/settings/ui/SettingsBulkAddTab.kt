package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import me.dvyy.tasks.app.ui.theme.Fonts
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.takeout.TakeoutViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsBulkAddTab(
    tasks: TasksViewModel = koinViewModel(),
    takeout: TakeoutViewModel = koinViewModel(),
) {
    var text by remember { mutableStateOf("") }
    BoxedList {
        MultilineSettingItem(
            "Bulk add tasks",
            "Enter tasks, one per line, use ^ for date, ! for highlight, : for text, ex",
            isLast = true
        ) {
            OutlinedTextField(
                text,
                onValueChange = { text = it },
                placeholder = {
                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontFamily = Fonts.monospaced(),
                                color = MaterialTheme.colorScheme.onSurface.fade(0.8f)
                            )
                        ) { append("!1 ^2024-12-31 :Do something important!") }
                    })
                },
                minLines = 8,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontFamily = Fonts.monospaced()),
            )
            TextButton(onClick = {
                //TODO bulk add
//            tasks.bulkAdd(text.lines())
                text = ""
            }) {
                Text("Done")
            }

            TextButton(onClick = {
                takeout.startImport()
            }) {
                Text("Import tasks")
            }

            TextButton(onClick = {
                takeout.startExport()
            }) {
                Text("Export tasks")
            }
        }
    }
}
