package me.dvyy.tasks.ui.elements.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
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
import me.dvyy.tasks.logic.Tasks.delete
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.state.AppConstants
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState

@Composable
fun TaskOptions(
    task: TaskState?,
    submitAction: (() -> Unit)? = null
) = Box(Modifier.padding(horizontal = AppConstants.taskTextPadding, vertical = 4.dp)) {
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
            Spacer(Modifier.weight(1f))
            if (submitAction != null) {
                FilledIconButton(onClick = submitAction) {
                    Icon(Icons.Outlined.Done, contentDescription = "Submit")
                }
            } else {
                val app = LocalAppState
                IconButton(onClick = { rememberedTask!!.delete(app) }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                }
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
        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Move") },
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
