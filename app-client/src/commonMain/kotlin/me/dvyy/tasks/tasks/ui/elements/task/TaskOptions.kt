package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.koin.compose.viewmodel.koinViewModel

sealed interface FocusedOption {
    data object None : FocusedOption
    data object Highlight : FocusedOption
}

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
    var focused: FocusedOption by remember { mutableStateOf(FocusedOption.None) }
    fun toggleFocused() {
        focused = if (focused == FocusedOption.Highlight) FocusedOption.None else FocusedOption.Highlight
    }
    Column(
        Modifier.padding(horizontal = ui.horizontalTaskTextPadding, vertical = UI.padding.sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(UI.padding.md)
        ) {
//            var dragged by remember { mutableStateOf(0f) }
            HighlightButton(
                task.highlight, task/*, modifier = Modifier.draggable(rememberDraggableState {
                dragged += it
                if(abs(dragged) > 50) {
                    setTask(task.copy(highlight = task.highlight.offsetBy(dragged.toInt() / 50)))
                    dragged = 0f
                }
            }, orientation = Orientation.Horizontal)*/
            ) { toggleFocused() }
            val today by time.today.collectAsState()
            TaskDatePicker(initialDate ?: today, interactions)
            Spacer(Modifier.weight(1f))
            if (submitAction != null) {
                FilledIconButton(onClick = submitAction) {
                    Icon(Icons.Outlined.Done, contentDescription = "Submit")
                }
            } else {
                IconButton(onClick = { interactions.onDelete() }, modifier = Modifier.size(ui.tasks.checkboxSize)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                }
            }
        }
        AnimatedVisibility(
            focused == FocusedOption.Highlight,
            enter = expandVertically(tween(150)),
            exit = shrinkVertically(tween(150))
        ) {
            HighlightButtons(
                task,
                setTask,
                ::toggleFocused,
                Modifier.height(ui.tasks.checkboxSize).fillMaxWidth().horizontalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun HighlightButtons(
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    toggleFocused: () -> Unit,
    modifier: Modifier = Modifier,
) = Column {
    HorizontalDivider(Modifier.fillMaxWidth())
//    var isLight by remember { mutableStateOf(task.highlight.isLight) }
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)) {
//        LightDarkHighlightToggle(isLight, onToggle = {
//            isLight = !isLight
//            setTask(task.copy(highlight = task.highlight.copy(isLight = isLight)))
//        })
        Highlight.Type.entries.forEach {
            HighlightButton(Highlight(it, true), task) { setTask(it); toggleFocused() }
        }
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)) {

        Highlight.Type.entries.forEach {
            HighlightButton(Highlight(it, false), task) { setTask(it); toggleFocused() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(initialDate: LocalDate, interactions: TaskInteractions, time: TimeViewModel = koinViewModel()) {
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
fun LightDarkHighlightToggle(isLight: Boolean, onToggle: () -> Unit) {
    val ui = LocalUIState.current
    IconButton(onClick = { onToggle() }, modifier = Modifier.size(ui.tasks.propertyButtonSize)) {
        Crossfade(isLight) {
            if (it) {
                Icon(Icons.Outlined.LightMode, contentDescription = "Light")
            } else {
                Icon(Icons.Outlined.DarkMode, contentDescription = "Dark")
            }
        }
    }
}

@Composable
fun HighlightButton(
    highlight: Highlight,
    task: TaskUiState,
    modifier: Modifier = Modifier,
    setTask: (TaskUiState) -> Unit,
) {
    CircleButton(onClick = { setTask(task.copy(highlight = highlight)) }, highlight.color, modifier = modifier)
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val ui = LocalUIState.current
    val border = if (color == Color.Transparent) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = color,
        ),
        onClick = onClick,
        modifier = modifier.size(ui.tasks.propertyButtonSize).focusProperties { canFocus = false },
        border = border,
    ) { content() }
}
