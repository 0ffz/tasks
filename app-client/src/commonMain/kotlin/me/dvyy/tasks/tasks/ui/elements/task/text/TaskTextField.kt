package me.dvyy.tasks.tasks.ui.elements.task.text

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
import me.dvyy.tasks.tasks.ui.elements.task.color
import me.dvyy.tasks.tasks.ui.state.TaskState

@Composable
fun TaskTextField(
    task: TaskState,
    focusRequested: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val textDecoration = if (task.uiState.completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        (if (task.selected) MaterialTheme.colorScheme.onSurface
        else task.uiState.highlight.color.getBestTextColor())
            .fade(if (task.uiState.completed) 0.3f else 1f)
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
    var selection by remember { mutableStateOf(TextRange(task.uiState.text.length)) }
    if (!task.selected) TaskTextPadding {
        Row(
            modifier = modifier,
        ) {
            //TODO emoji tags
//            Text(buildString {
//                if (task.uiState.text.hashCode() % 3 == 0)
//                    append("🔥")
//                if (task.uiState.text.hashCode() % 2 == 0)
//                    append("❗")
//            }, modifier = Modifier.drawBehind {
//                if (task.uiState.highlight != Highlight.Unmarked)
//                    drawRect(Color.Black.copy(alpha = 0.3f), topLeft = Offset(-3.dp.toPx(), -1.dp.toPx()), size = size.copy(width = size.width + 4.dp.toPx(), height = size.height + 2.dp.toPx()))
////                drawRect(Color.Black.copy(alpha = 0.3f), topLeft = Offset(-5.dp.toPx(), -5.dp.toPx()), size = size.copy(height = size.height + 10.dp.toPx()))
//            })
//            Spacer(Modifier.width(UI.padding.sm))

            Text(
                text = task.uiState.text,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else BasicTextField(
        value = TextFieldValue(task.uiState.text, selection),
        readOnly = task.uiState.completed,
        singleLine = false,
        onValueChange = { new ->
            task.updateUi { it.copy(text = new.text) }
            selection = new.selection
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = textStyle,
        keyboardActions = task.keyboardActions,
        keyboardOptions = task.keyboardOptions,
        decorationBox = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TaskTextPadding { it() }
            }

        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused) task.mutate.onSelect()
            }
            .fillMaxWidth()
            .onPreviewKeyEvent { key ->
                // Handle some multiline features that aren't correctly supported by BasicTextField
                when {
                    key.isCtrlPressed -> {
                        selection = when (key.key) {
                            Key.MoveHome -> TextRange.Zero
                            Key.MoveEnd -> TextRange(task.uiState.text.length)
                            else -> return@onPreviewKeyEvent false
                        }
                    }

                    key.isShiftPressed -> when (key.key) {
                        Key.Enter -> {
                            task.updateUi {
                                it.copy(
                                    text = it.text.substring(0, selection.start)
                                            + "\n" + it.text.substring(selection.end)
                                )
                            }
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
