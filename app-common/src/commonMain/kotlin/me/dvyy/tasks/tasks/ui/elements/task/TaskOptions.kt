package me.dvyy.tasks.tasks.ui.elements.task

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.koin.compose.koinInject

@Composable
fun TaskOptions(
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    initialDate: LocalDate? = null,
    interactions: TaskInteractions,
    submitAction: (() -> Unit)? = null,
    time: TimeViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    Box(Modifier.padding(horizontal = ui.horizontalTaskTextPadding, vertical = 4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HighlightButton(Highlight.Unmarked, task, setTask)
                HighlightButton(Highlight.Important, task, setTask)
                HighlightButton(Highlight.InProgress, task, setTask)
                TaskDatePicker(initialDate ?: time.today, interactions)

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
fun TaskDatePicker(initialDate: LocalDate, interactions: TaskInteractions, time: TimeViewModel = koinInject()) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDayIn(time.timezone).toEpochMilliseconds()
    )

    AssistChip(
        label = {
            Text(
                "Move",
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        },
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
fun HighlightButton(highlight: Highlight, task: TaskUiState, setTask: (TaskUiState) -> Unit) {
    val ui = LocalUIState.current
    val border = if (highlight == Highlight.Unmarked) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = highlight.color,
        ),
        onClick = { setTask(task.copy(highlight = highlight)) },
        modifier = Modifier.size(ui.taskOptionSize).focusProperties { canFocus = false },
        border = border,
    ) { }
}
