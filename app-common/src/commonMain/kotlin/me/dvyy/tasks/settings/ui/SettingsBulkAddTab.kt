package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.theme.Fonts
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel

@Composable
fun SettingsBulkAddTab(
    tasks: TasksViewModel = koinViewModel(),
) {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(buildAnnotatedString {
            append("Enter tasks, one per line, use ^ for date, ! for highlight, : for text, ex")
            appendLine()
            withStyle(SpanStyle(fontFamily = Fonts.monospaced())) { append("!1 ^2024-12-31 :Do something important!") }
        })
        OutlinedTextField(
            text,
            onValueChange = { text = it },
            minLines = 8,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontFamily = Fonts.monospaced()),
        )

        TextButton(onClick = {
            tasks.bulkAdd(text.lines())
            text = ""
        }) {
            Text("Done")
        }
    }
}
