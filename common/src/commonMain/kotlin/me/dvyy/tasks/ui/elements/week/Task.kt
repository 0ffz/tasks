package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.TaskAlt
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
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.delete
import me.dvyy.tasks.platforms.onHoverIfAvailable
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple

@Immutable
data class TaskInteractions(
    val onKeyEvent: (KeyEvent) -> Boolean = { false },
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val onNameChange: (String) -> Unit = {},
)

@Composable
fun Task(
    task: TaskState,
    interactions: TaskInteractions = TaskInteractions(),
) {
    val app = LocalAppState
    var isHovered by remember { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = Modifier
            .onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            )
            .heightIn(min = AppConstants.taskHeight)
            .focusProperties { canFocus = false }
            .clickableWithoutRipple { app.selectedTask.value = task }
            .onKeyEvent(interactions.onKeyEvent)
    ) {
        val active by task.isActive(app)
        LaunchedEffect(Unit) {
            snapshotFlow { active }
                .drop(1)
                .collect {
                    println("Deleting $active")
                    if (!active && task.name.value.isEmpty()) task.delete(app)
                }
        }

        println("Recomposing task")
        TaskSelectedSurface(active) {
            val completed by task.completed.collectAsState()
            val alpha by animateFloatAsState(if (completed) 0.3f else 1f)
            Column(Modifier.alpha(alpha)) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    TaskHighlight(task)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val isSmall by app.isSmallScreen.collectAsState()

                        TaskTextField(active, completed, task, interactions, Modifier.weight(1f, true)
                            .onFocusEvent {
                                if (it.isFocused) app.selectedTask.value = task
                            })
                        if (isSmall || isHovered)
                            TaskCheckBox(completed, task)
                    }
                }
                AnimatedVisibility(
                    active,
                    enter = fadeIn(tween(delayMillis = 100)) + expandVertically(),
                    exit = fadeOut(tween(durationMillis = 100)) + shrinkVertically(),
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGestures { _, _ -> }
                        }
                ) {
                    TaskOptions(task)
                }
            }
        }
    }
}

@Composable
fun TaskHighlight(task: TaskState) {
    val highlight by task.highlight.collectAsState()
    val name by task.name.collectAsState()
    val adjustedHighlight by animateColorAsState(highlight.color)
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.height(AppConstants.taskHighlightHeight)
    ) {
        TaskTextPadding {
            Text(name, Modifier.alpha(0f))
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
        tonalElevation = elevation.dp, //CardDefaults.elevatedCardElevation()
    ) {
        content()
    }
}

@Composable
fun TaskTextField(
    active: Boolean,
    completed: Boolean,
    task: TaskState,
    interactions: TaskInteractions,
    modifier: Modifier = Modifier,
) {
    val app = LocalAppState
    val taskName by task.name.collectAsState()
    val textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        MaterialTheme.colorScheme.onSurface
    )
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        textDecoration = textDecoration,
    )
    val focusRequester = remember { FocusRequester() }
    val focusRequested by task.focusRequested.collectAsState()


//    if (!active) {
//        TaskTextPadding(modifier) {
//            Text(
//                taskName,
//                style = textStyle,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.focusRequester(focusRequester)
//            )
//        }
//        return
//    }

    // Otherwise render full text field
    LaunchedEffect(focusRequested) {
        if (focusRequested) {
            task.focusRequested.value = false
            focusRequester.requestFocus()
        }
    }
    BasicTextField(
        value = taskName,
        readOnly = completed || (!active),
        singleLine = true,
        onValueChange = interactions.onNameChange,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = textStyle,
        keyboardActions = interactions.keyboardActions,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTextPadding { innerTextField() }
            }
        },
        modifier = modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
    )
}

@Composable
fun TaskTextPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier.height(AppConstants.taskHeight).padding(AppConstants.taskTextPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

@Composable
fun TaskCheckBox(completed: Boolean, task: TaskState) {
    IconButton(
        onClick = { task.completed.update { !completed } },
        colors = IconButtonDefaults.iconButtonColors(),
        modifier = Modifier.size(AppConstants.taskHeight)
    ) {
        when {
            completed -> Icon(Icons.Rounded.TaskAlt, contentDescription = "Completed")
            else -> Icon(Icons.Rounded.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}
