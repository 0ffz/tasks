package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.state.TaskState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun Task(
    task: TaskState,
    onNameChange: (String) -> Unit,
    highlight: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    onTab: () -> Unit,
) {
    val adjustedHighlight by animateColorAsState(if (task.completed && highlight != Color.Transparent) highlight.copy(alpha = 0.1f) else highlight)
    val textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        MaterialTheme.colorScheme.onPrimaryContainer.run { if (task.completed) copy(alpha = 0.3f) else this }
    )

    Surface(
        color = adjustedHighlight,
        modifier = Modifier.padding(4.dp).height(34.dp).then(modifier),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        var active by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) { active = true }
            .onPointerEvent(PointerEventType.Exit) { active = false }) {
            BasicTextField2(
                value = task.name,
                enabled = !task.completed,
                lineLimits = TextFieldLineLimits.SingleLine,
                onValueChange = onNameChange,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodyLarge
                    .copy(
                        color = textColor,
                        textDecoration = textDecoration,
                    ),
                decorator = { innerTextField ->
                    Row(
                        Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) { innerTextField() }
                },
                modifier = Modifier.weight(1f, true)
                    .fillMaxSize()
                    .onKeyEvent { event ->
                        if (event.isCtrlPressed && event.key == Key.E && event.type == KeyEventType.KeyDown) {
                            onTab()
                            true
                        } else false
                    }
            )
            if (active) {
                IconButton(
                    onClick = { task.completed = !task.completed },
                    colors = IconButtonDefaults.iconButtonColors(),
                ) {
                    if (task.completed) {
                        Icon(Icons.Rounded.TaskAlt, contentDescription = "Completed")
                    } else {
                        Icon(Icons.Rounded.RadioButtonUnchecked, contentDescription = "Mark as completed")
                    }
                }
            }
        }
    }
}
