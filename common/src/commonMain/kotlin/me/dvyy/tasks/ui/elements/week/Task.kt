package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.logic.Tasks.delete
import me.dvyy.tasks.platforms.onHoverIfAvailable
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState

@Composable
fun Task(
    task: TaskState,
    onNameChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    onNext: () -> Unit = {},
) {
    val completed by task.completed.collectAsState()
    val highlight by task.highlight.collectAsState()
    val adjustedHighlight by animateColorAsState(
        if (completed && highlight.color != Color.Transparent) highlight.color.copy(
            alpha = 0.1f
        ) else highlight.color
    )
    val app = LocalAppState

    val textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        MaterialTheme.colorScheme.onPrimaryContainer.run { if (completed) copy(alpha = 0.3f) else this }
    )
    Surface(
        color = adjustedHighlight,
        modifier = Modifier.padding(2.dp).height(34.dp).then(modifier).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { app.selectedTask.value = task },
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {

        var isHovered by remember { mutableStateOf(false) }
        val active by app.selectedTask
            .map { it == task }
            .collectAsState(false)
        val selectedColor = MaterialTheme.colorScheme.surfaceVariant
        val selectedBg by animateColorAsState(remember(active) { if (active) selectedColor else Color.Transparent })
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            ).padding(start = 4.dp).background(selectedBg)
        ) {
            val taskName by task.name.collectAsState()
            var focused by remember { mutableStateOf(false) }
            val textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = textColor,
                textDecoration = textDecoration,
            )
            val focusRequester = remember { FocusRequester() }
            val focusRequested by task.focusRequested.collectAsState()
            var hasBeenFocused by remember { mutableStateOf(false) }

            println("Recomposing ${task.name.value}")
            if (!active) Text(
                taskName,
                Modifier.fillMaxWidth().weight(1f, true).focusRequester(focusRequester),
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            else {
                LaunchedEffect(focusRequested) {
                    if (focusRequested) {
                        task.focusRequested.value = false
                        focusRequester.requestFocus()
                    }
                }
                BasicTextField(
                    value = taskName,
                    enabled = !completed && active,
                    singleLine = true,
                    onValueChange = onNameChange,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = textStyle,
                    keyboardActions = KeyboardActions(onNext = { onNext() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    decorationBox = { innerTextField ->
                        Row(
//                        Modifier.padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) { innerTextField() }
                    },
                    modifier = Modifier.weight(1f, true)
                        .fillMaxSize()
                        .onKeyEvent(onKeyEvent)
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

            if (active || isHovered) {
                IconButton(
                    onClick = { task.completed.update { !completed } },
                    colors = IconButtonDefaults.iconButtonColors(),
                ) {
                    if (completed) {
                        Icon(Icons.Rounded.TaskAlt, contentDescription = "Completed")
                    } else {
                        Icon(Icons.Rounded.RadioButtonUnchecked, contentDescription = "Mark as completed")
                    }
                }
            }
        }
    }
}
