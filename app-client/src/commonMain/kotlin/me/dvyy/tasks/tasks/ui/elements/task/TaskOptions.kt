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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.getBestTextColor
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

sealed interface FocusedOption {
    data object None : FocusedOption
    data object Highlight : FocusedOption
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    Column {
        Box(Modifier.height(UI.tasks.propertyButtonSize)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
//            horizontalArrangement = Arrangement.spacedBy(UI.padding.md)
            ) {
//            var dragged by remember { mutableStateOf(0f) }
                HighlightButton(
                    task.highlight, task,/*, modifier = Modifier.draggable(rememberDraggableState {
                dragged += it
                if(abs(dragged) > 50) {
                    setTask(task.copy(highlight = task.highlight.offsetBy(dragged.toInt() / 50)))
                    dragged = 0f
                }
            }, orientation = Orientation.Horizontal)*/
                    setTask = { toggleFocused() }
                ) {
                    Icon(Icons.Outlined.Tag, contentDescription = "Tag", Modifier.size(18.dp))
                }
                val today by time.today.collectAsState()
                TaskDatePicker(initialDate ?: today, interactions)
                Spacer(Modifier.weight(1f))
                if (submitAction != null) {
                    FilledIconButton(onClick = submitAction) {
                        Icon(Icons.Outlined.Done, contentDescription = "Submit")
                    }
                } else {
                    BoxButton(
                        onClick = { interactions.onDelete() },
//                        shape = RoundedCornerShape(bottomEnd = 16.dp),
//                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    ) {

                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", Modifier.size(18.dp))
                    }
//                    FilledTonalIconButton(
//
//                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//                        modifier = Modifier.height(UI.tasks.checkboxSize)
//                    ) {
//                    }
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
//    HorizontalDivider(Modifier.fillMaxWidth())
    Row(
        modifier,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)
    ) {
//        LightDarkHighlightToggle(isLight, onToggle = {
//            isLight = !isLight
//            setTask(task.copy(highlight = task.highlight.copy(isLight = isLight)))
//        })
        Highlight.Type.entries.forEach { type ->
            val highlight = Highlight(type, true)
            HighlightButton(
                highlight,
                task,
                setTask = { setTask(it.copy(highlight = highlight)); toggleFocused() }
            )
        }
    }
    Row(
        modifier,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)
    ) {

        Highlight.Type.entries.forEach { type ->
            val highlight = Highlight(type, false)
            HighlightButton(
                highlight,
                task,
                setTask = {
                    setTask(it.copy(highlight = highlight)); toggleFocused()
                }
            )
        }
    }
}

@Composable
fun BoxButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RoundedCornerShape(UI.size.sm),
    border: BorderStroke? = null,//BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Surface(
    modifier = modifier.size(UI.tasks.propertyButtonSize).padding(UI.padding.sm),
    onClick = onClick,
    color = color,
    shape = shape,
    border = border,
    contentColor = contentColor,
) {
    Box(contentAlignment = Alignment.Center) {
        content()
    }
}


@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskDatePicker(initialDate: LocalDate, interactions: TaskInteractions, time: TimeViewModel = koinViewModel()) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDayIn(time.timezone).toEpochMilliseconds()
    )

    BoxButton(
        onClick = { showDatePicker = true },
    ) {
        Icon(Icons.Outlined.CalendarMonth, contentDescription = "Move task", Modifier.size(18.dp))
    }
//    AssistChip(
//        label = {
//            Text(
//                "Move",
//                maxLines = 1,
//                overflow = TextOverflow.Clip,
//            )
//        },
//        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Move") },
//        onClick = { showDatePicker = true },
//    )
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
    content: @Composable () -> Unit = {},
) {
    SquareButton(onClick = { setTask(task.copy(highlight = highlight)) }, highlight.color, modifier = modifier) {
        content()
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val ui = LocalUIState.current
    OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = color,
        ),
        onClick = onClick,
        modifier = modifier.size(ui.tasks.height).focusProperties { canFocus = false }
//            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), CircleShape),
//        border = border,
    ) { content() }
}

@Composable
fun SquareButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    LocalUIState.current
    BoxButton(
        onClick = onClick,
//        borderShape = RoundedCornerShape(bottomStart = 16.dp),
        color = color,
        contentColor = color.getBestTextColor(),
        modifier = Modifier
            .focusProperties { canFocus = false }
    ) { content() }
}
