package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.core.ui.getBestTextColor
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Composable
fun Task(
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    selected: Boolean,
    interactions: TaskInteractions,
    focusRequested: Boolean = false,
    date: LocalDate? = null,
) {
    var isHovered by remember { mutableStateOf(false) }
    val ui = LocalUIState.current
    val selectedState by rememberUpdatedState(selected)
    LaunchedEffect(task) {
        snapshotFlow { selectedState }
            .drop(1)
            .filter { !it } // Listen to deselect
            .collect {
                if (task.text.isEmpty()) interactions.onDelete()
            }
    }

    BoxWithConstraints(
        modifier = Modifier
            .onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            )
            .heightIn(min = ui.taskHeight)
            .focusProperties { canFocus = false }
            .clickableWithoutRipple {
                interactions.onSelect()
            } // Consume click so background (deselect) doesn't get it
            .onPreviewKeyEvent(interactions::onKeyEvent)
    ) {
        TaskSelectedSurface(
            selected,
            task.highlight,
        ) {
            Column {
                Box(
                    modifier = Modifier.padding(horizontal = ui.horizontalTaskTextPadding),
//                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(Modifier.weight(1f, true), contentAlignment = Alignment.CenterStart) {
                            if (!selected) TaskHighlight(task.text, task.highlight, task.completed)
                            TaskTextField(task, selected, setTask, interactions, focusRequested, Modifier)
                        }
                        val responsive = LocalUIState.current

                        if (responsive.alwaysShowCheckbox || isHovered || selected)
                            TaskCheckBox(task, setTask)
                    }
                }
                AnimatedVisibility(
                    selected,
                    enter = fadeIn(tween(delayMillis = 100)) + expandVertically(),
                    exit = fadeOut(tween(durationMillis = 100)) + shrinkVertically(),
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGestures { _, _ -> }
                        }
                ) {
                    TaskOptions(task, setTask, date, interactions)
                }
            }
        }
    }
}

@Composable
fun TaskHighlight(text: String, highlight: Highlight, completed: Boolean = false) {
    val ui = LocalUIState.current
    val adjustedHighlight by animateColorAsState(highlight.color.fade(if (completed) ui.completedFade else 1f))
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.height(ui.taskHighlightHeight),
    ) {
        TaskTextPadding {
            Text(text, Modifier.alpha(0f))
        }
    }
}

@Composable
fun TaskSelectedSurface(
    visible: Boolean,
    highlight: Highlight,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val defaultColor = CardDefaults.elevatedCardColors().containerColor
    val elevation by animateFloatAsState(if (visible) 1f else 0f)
    val fullCornerSize = 20.dp
    val cornerShape by animateDpAsState(if (visible) fullCornerSize else 0.dp)
    val padding by animateDpAsState(if (visible) 10.dp else 0.dp)
    val highlightColor = highlight.color
        .copy(alpha = 0.15f)
        .takeIf { visible && highlight != Highlight.Unmarked } ?: Color.Transparent
    val animatedHighlight by animateColorAsState(highlightColor)
    Surface(
        modifier = modifier.padding(vertical = padding),
        shape = RoundedCornerShape(cornerShape),
        color = defaultColor,
        tonalElevation = elevation.dp,
    ) {
        Surface(
            color = animatedHighlight,
            shape = RoundedCornerShape(fullCornerSize),
        ) {
            content()
        }
    }
}

@Composable
fun TaskTextField(
    task: TaskUiState,
    selected: Boolean,
    setTask: (TaskUiState) -> Unit,
    interactions: TaskInteractions,
    focusRequested: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        (if (selected) MaterialTheme.colorScheme.onSurface
        else task.highlight.color.getBestTextColor())
            .fade(if (task.completed) 0.3f else 1f)
    )
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        textDecoration = textDecoration,
    )
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusRequested) {
        if (focusRequested) {
            focusRequester.requestFocus()
        }
    }
    var selection by remember { mutableStateOf(TextRange(task.text.length)) }
    if (!selected) TaskTextPadding {
        Text(
            text = task.text,
            style = textStyle,
            modifier = modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    } else BasicTextField(
        value = TextFieldValue(task.text, selection),
        readOnly = task.completed,
        singleLine = !selected,
        onValueChange = {
            setTask(task.copy(text = it.text))
            selection = it.selection
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = textStyle,
        keyboardActions = interactions.keyboardActions,
        keyboardOptions = interactions.keyboardOptions,
        decorationBox = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTextPadding { it() }
            }

        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused) interactions.onSelect()
            }
            .fillMaxWidth()
            .onPreviewKeyEvent {
                // Handle some multiline features that aren't correctly supported by BasicTextField
                when {
                    it.isCtrlPressed -> {
                        selection = when (it.key) {
                            Key.Home -> TextRange.Zero
                            Key.MoveEnd -> TextRange(task.text.length)
                            else -> return@onPreviewKeyEvent false
                        }
                    }

                    it.isShiftPressed -> when (it.key) {
                        Key.Enter -> {
                            setTask(
                                task.copy(
                                    text = task.text.substring(
                                        0,
                                        selection.start
                                    ) + "\n" + task.text.substring(selection.end)
                                )
                            )
                            selection = TextRange(selection.start + 1)
                        }

                        else -> return@onPreviewKeyEvent false
                    }

                    else -> return@onPreviewKeyEvent false
                }
                true
            }
    )
}

@Composable
fun TaskTextPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val ui = LocalUIState.current
    Box(
        modifier/*.height(ui.taskHeight)*/.padding(ui.taskTextPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

@Composable
fun TaskCheckBox(task: TaskUiState, setTask: (TaskUiState) -> Unit) {
    val ui = LocalUIState.current
    IconButton(
        onClick = { setTask(task.copy(completed = !task.completed)) },
        colors = IconButtonDefaults.iconButtonColors().let {
            it.copy(contentColor = it.contentColor.fade(if (task.completed) ui.completedFade else 1f))
        },
        modifier = Modifier.size(ui.taskCheckboxSize)
    ) {
        when {
            task.completed -> Icon(Icons.Outlined.TaskAlt, contentDescription = "Completed")
            else -> Icon(Icons.Outlined.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
//    Checkbox(
//        checked = task.completed,
//        onCheckedChange = { setTask(task.copy(completed = it)) },
//        modifier = Modifier.size(ui.taskCheckboxSize)
//    )
}

