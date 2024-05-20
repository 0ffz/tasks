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
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.TimeState
import me.dvyy.tasks.stateholder.TaskInteractions
import me.dvyy.tasks.ui.elements.week.TaskListKey
import org.koin.compose.koinInject

@Composable
fun TaskOptions(
    listKey: TaskListKey,
    interactions: TaskInteractions,
    submitAction: (() -> Unit)? = null
) {
    val ui = LocalUIState.current
    Box(Modifier.padding(horizontal = ui.taskTextPadding, vertical = 4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HighlightButton(Highlight.Unmarked, interactions)
                HighlightButton(Highlight.Important, interactions)
                HighlightButton(Highlight.InProgress, interactions)
                if (listKey is TaskListKey.Date) { //TODO decide on separate or combined date/list pickers
                    TaskDatePicker(listKey.date, interactions)
                }
                Spacer(Modifier.weight(1f))
                if (submitAction != null) {
                    FilledIconButton(onClick = submitAction) {
                        Icon(Icons.Outlined.Done, contentDescription = "Submit")
                    }
                } else {
                    IconButton(onClick = { interactions.onDelete() }, modifier = Modifier.size(ui.taskCheckboxSize)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(date: LocalDate, interactions: TaskInteractions, time: TimeState = koinInject()) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDayIn(time.timezone).toEpochMilliseconds()
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
                val dateMillis = datePickerState.selectedDateMillis ?: return@TextButton
                val newDate = Instant.fromEpochMilliseconds(dateMillis).toLocalDateTime(TimeZone.UTC).date
                interactions.onListChanged(newDate)
                showDatePicker = false
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
    ) {
        DatePicker(datePickerState)
    }
}

@Composable
fun HighlightButton(highlight: Highlight, interactions: TaskInteractions) {
    val ui = LocalUIState.current
    val border = if (highlight == Highlight.Unmarked) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = highlight.color,
        ),
        onClick = { interactions.onHighlightChanged(highlight) },
        modifier = Modifier.size(ui.taskHighlightHeight).focusProperties { canFocus = false },
        border = border,
    ) { }
}
