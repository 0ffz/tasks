package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.core.ui.getBestTextColor
import me.dvyy.tasks.tasks.ui.TaskInteractions
import me.dvyy.tasks.tasks.ui.state.TaskUiState

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
        else task.highlight.getBestTextColor())
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
