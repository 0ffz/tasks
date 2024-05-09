package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.delete
import me.dvyy.tasks.platforms.onHoverIfAvailable
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState
import me.dvyy.tasks.ui.AppConstants
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple

@Immutable
data class TaskInteractions(
    val onKeyEvent: (KeyEvent) -> Boolean,
    val keyboardActions: KeyboardActions,
    val onNameChange: (String) -> Unit,
)

@Composable
fun Task(
    task: TaskState,
    modifier: Modifier = Modifier,
    interactions: TaskInteractions,
) {
    val app = LocalAppState
    var isHovered by remember { mutableStateOf(false) }
//    val highlight by task.highlight.collectAsState()
//    val adjustedHighlight by animateColorAsState(
//        if (completed && highlight.color != Color.Transparent) highlight.color.copy(
//            alpha = 0.1f
//        ) else highlight.color
//    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            )
            .height(AppConstants.taskHeight)
            .clickableWithoutRipple { app.selectedTask.value = task }
            .onKeyEvent(interactions.onKeyEvent)
            .then(modifier)
    ) {
        val active by task.isActive(app)
        TaskSelectedSurface(active)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val completed by task.completed.collectAsState()

            TaskTextField(active, completed, task, interactions, Modifier.weight(1f, true))
            if (active || isHovered) TaskCheckBox(completed, task)
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
        MaterialTheme.colorScheme.onPrimaryContainer.run { if (completed) copy(alpha = 0.3f) else this }
    )
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = textColor,
        textDecoration = textDecoration,
    )
    val focusRequester = remember { FocusRequester() }

    if (!active) {
        TaskTextPadding(modifier.focusRequester(focusRequester)) {
            Text(
                taskName,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        return
    }

    // Otherwise render full text field
    var focused by remember { mutableStateOf(false) }
    val focusRequested by task.focusRequested.collectAsState()
    var hasBeenFocused by remember { mutableStateOf(false) }

    LaunchedEffect(focusRequested) {
        if (focusRequested) {
            task.focusRequested.value = false
            focusRequester.requestFocus()
        }
    }
    BasicTextField(
        value = taskName,
        readOnly = completed || !active,
        singleLine = true,
        onValueChange = interactions.onNameChange,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = textStyle,
        keyboardActions = interactions.keyboardActions,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) { TaskTextPadding { innerTextField() } }
        },
        modifier = modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .onFocusEvent {
                focused = it.isFocused
                if (it.isFocused) {
                    hasBeenFocused = true
                } else if (hasBeenFocused && taskName.isEmpty()) {
                    task.delete(app)
                }
            }
    )
}

@Composable
fun TaskTextPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
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
