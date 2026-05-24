package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val importProgress by takeout.importProgress.collectAsState()
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
        }
    }
    SettingsButtonGroup {
        PrimaryButton(onClick = {
            //TODO bulk add
//            tasks.bulkAdd(text.lines())
            text = ""
        }) {
            Text("Done")
        }

        SecondaryButton(onClick = {
            takeout.startImport()
        }, enabled = importProgress == null) {
            Text("Import tasks")
        }
        importProgress?.let { progress ->
            if (progress.total > 0) {
                LinearProgressIndicator(
                    progress = { progress.current.toFloat() / progress.total },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Importing ${progress.current} / ${progress.total}")
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Parsing file...")
            }
        }

        SecondaryButton(onClick = {
            takeout.startExport()
        }) {
            Text("Export tasks")
        }

        SecondaryButton(onClick = {
            takeout.migrateOldDatabase()
        }) {
            Text("Migrate tasks v1")
        }
    }
}
