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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.Task
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.TaskInteractions

@Composable
fun Task(
    task: Task,
    selected: Boolean,
    interactions: TaskInteractions,
) {
    var isHovered by remember { mutableStateOf(false) }
    val ui = LocalUIState.current
    val selectedState by rememberUpdatedState(selected)

    LaunchedEffect(task) {
        snapshotFlow { selectedState }
            .distinctUntilChanged()
            .drop(1)
            .filter { !it } // Listen to deselect
            .collect {
                println("Select changed $it for ${task.name}")
                if (task.name.isEmpty()) interactions.onDelete()
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
            .clickableWithoutRipple { interactions.onSelect() }
            .onKeyEvent(interactions.onKeyEvent)
    ) {
        TaskSelectedSurface(selected) {
            val alpha by animateFloatAsState(if (task.completed) 0.3f else 1f)
            Column(Modifier.alpha(alpha)) {
                Box(
                    modifier = Modifier.padding(horizontal = ui.taskTextPadding),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    TaskHighlight(task.name, task.highlight)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val responsive = LocalUIState.current

                        TaskTextField(task.name, task.completed, selected, interactions, Modifier.weight(1f, true))
                        if (responsive.alwaysShowCheckbox || isHovered || selected)
                            TaskCheckBox(task.completed, interactions)
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
                    TaskOptions(task.key, interactions)
                }
            }
        }
    }
}

//@Composable
//fun QueueSaveWhenModified(dateState: DateState, task: TaskState) {
//    val name by task.name.collectAsState()
//    val date by task.date.collectAsState()
//    val completed by task.completed.collectAsState()
//    val highlight by task.highlight.collectAsState()
//    val app = LocalAppState
//    LaunchedEffect(dateState, task) {
//        // Drop 1 to ignore initial state, for existing tasks this means they're already saved, for new ones, it's the empty state
//        snapshotFlow { arrayOf(name, date, completed, highlight) }.drop(1).collect {
//            task.syncStatus.value = SyncStatus.LOCAL_MODIFIED
//            app.queueSaveDay(dateState)
//        }
//    }
//}

@Composable
fun TaskHighlight(title: String, highlight: Highlight) {
    val ui = LocalUIState.current
    val adjustedHighlight by animateColorAsState(highlight.color)
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.height(ui.taskHighlightHeight)
    ) {
        TaskTextPadding {
            Text(title, Modifier.alpha(0f))
        }
    }
}

@Composable
fun TaskSelectedSurface(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val elevation by animateFloatAsState(if (visible) 1f else 0f)
    val cornerShape by animateDpAsState(if (visible) 20.dp else 0.dp)
    val padding by animateDpAsState(if (visible) 10.dp else 0.dp)
    Surface(
        modifier = Modifier.padding(vertical = padding),
        shape = RoundedCornerShape(cornerShape),
        color = CardDefaults.elevatedCardColors().containerColor,
        tonalElevation = elevation.dp,
    ) {
        content()
    }
}

@Composable
fun TaskTextField(
    title: String,
    completed: Boolean,
    selected: Boolean,
    interactions: TaskInteractions,
    modifier: Modifier = Modifier,
) {
    val textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        MaterialTheme.colorScheme.onSurface
    )
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        textDecoration = textDecoration,
    )
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(selected) {
        if (selected) focusRequester.requestFocus()
//        else focusManager.clearFocus()
    }

    BasicTextField(
        value = title,
        readOnly = completed,// || (!active),
        singleLine = true,
        onValueChange = interactions.onTitleChanged,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = textStyle,
        keyboardActions = interactions.keyboardActions,
        keyboardOptions = interactions.keyboardOptions,
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTextPadding { innerTextField() }
            }
        },
        modifier = modifier
//            .fillMaxHeight()
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused) interactions.onSelect()
            }
    )
}

@Composable
fun TaskTextPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val ui = LocalUIState.current
    Box(
        modifier.height(ui.taskHeight).padding(ui.taskTextPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

@Composable
fun TaskCheckBox(completed: Boolean, interactions: TaskInteractions) {
    val ui = LocalUIState.current
    IconButton(
        onClick = { interactions.onCheckChanged(!completed) },
        colors = IconButtonDefaults.iconButtonColors(),
        modifier = Modifier.size(ui.taskCheckboxSize)
    ) {
        when {
            completed -> Icon(Icons.Outlined.TaskAlt, contentDescription = "Completed")
            else -> Icon(Icons.Outlined.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}
