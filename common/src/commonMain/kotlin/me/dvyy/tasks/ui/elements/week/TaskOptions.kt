package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import me.dvyy.tasks.logic.Tasks.changeDate
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants

@Composable
fun TaskOptions(task: TaskState?) = Box(Modifier) {
    Box(Modifier.padding(horizontal = AppConstants.taskTextPadding, vertical = 4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val rememberedTask by snapshotFlow { task }.filterNotNull().collectAsState(task)
            if (rememberedTask == null) return
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HighlightButton(rememberedTask!!, Highlight.Unmarked)
                HighlightButton(rememberedTask!!, Highlight.Important)
                HighlightButton(rememberedTask!!, Highlight.InProgress)
                TaskDatePicker(rememberedTask!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(task: TaskState) {
    var showDatePicker by remember { mutableStateOf(false) }
    val taskDate by task.date.collectAsState()
    val app = LocalAppState
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = taskDate.atStartOfDayIn(app.timezone).toEpochMilliseconds()
    )

    AssistChip(
        label = { Text("Move") },
        leadingIcon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = "Move") },
        onClick = { showDatePicker = true },
    )
    if (showDatePicker) DatePickerDialog(
        onDismissRequest = { showDatePicker = false },
        confirmButton = {
            TextButton(onClick = {
                val selected = datePickerState.selectedDateMillis ?: return@TextButton
                println(Instant.fromEpochMilliseconds(selected))
                task.changeDate(app, Instant.fromEpochMilliseconds(selected).toLocalDateTime(TimeZone.UTC).date)
                showDatePicker = false
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
    ) {
        DatePicker(datePickerState)
    }
}

@Composable
fun HighlightButton(task: TaskState, highlight: Highlight) {
    val border = if (highlight == Highlight.Unmarked) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = highlight.color,
        ),
        onClick = { task.highlight.value = highlight },
        modifier = Modifier.size(AppConstants.taskHighlightHeight).focusProperties { canFocus = false },
        border = border,
    ) { }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
