package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
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
    println("Recomposing task ${task.name.value}")

    var focused by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            )
            .height(AppConstants.taskHeight)
            .clickableWithoutRipple { app.selectedTask.value = task }
            .onKeyEvent(interactions.onKeyEvent)
    ) {
        val active by task.isActive(app)
        TaskSelectedSurface(active)
        Box(
            Modifier.padding(horizontal = 8.dp).fillMaxHeight(),
            contentAlignment = Alignment.CenterStart,
        ) {
            TaskHighlight(task)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.focusGroup(),
            ) {
                val completed by task.completed.collectAsState()

                TaskTextField(active, completed, task, interactions, Modifier.weight(1f, true))
                if (active || isHovered) TaskCheckBox(completed, task)
            }
        }
    }
}

@Composable
fun TaskHighlight(task: TaskState) {
    val highlight by task.highlight.collectAsState()
    val completed by task.completed.collectAsState()
    val name by task.name.collectAsState()
    val adjustedHighlight by animateColorAsState(
        if (completed && highlight.color != Color.Transparent) highlight.color.copy(
            alpha = 0.1f
        ) else highlight.color
    )
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
    ) {
        TaskTextPadding {
            Text(name, Modifier.alpha(0f))
        }
    }
}

@Composable
fun TaskSelectedSurface(visible: Boolean) {
    val elevation by animateFloatAsState(if (visible) 1f else 0f)
    val color = if (elevation == 0f) Color.Transparent else MaterialTheme.colorScheme.surface
    Surface(
        Modifier.fillMaxSize(),
        color = color,
        tonalElevation = elevation.dp,
    ) { }
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
        MaterialTheme.colorScheme.onSurface.run { if (completed) copy(alpha = 0.3f) else this }
    )
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        textDecoration = textDecoration,
    )
    val focusRequester = remember { FocusRequester() }
    val focusRequested by task.focusRequested.collectAsState()
    if (!active) {
        TaskTextPadding(modifier) {
            Text(
                taskName,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }
        return
    }

    // Otherwise render full text field
    LaunchedEffect(focusRequested) {
        if (focusRequested) {
            task.focusRequested.value = false
            focusRequester.requestFocus()
        }
    }
//
//    DisposableEffect(activeOnlyFalse) {
//        println("Running delete with $activeOnlyFalse")
//        if (!activeOnlyFalse && task.name.value.isEmpty()) {
//            task.delete(app)
//        }
//    }
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
        modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
        modifier = Modifier.size(AppConstants.taskHeight).fillMaxSize()
    ) {
        when {
            completed -> Icon(Icons.Rounded.TaskAlt, contentDescription = "Completed")
            else -> Icon(Icons.Rounded.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}
