package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Task(
    name: String,
    onNameChange: (String) -> Unit,
    highlight: Color = Color.Transparent,
    onTab: () -> Unit,
) {
    var completed by remember { mutableStateOf(false) }
    val adjustedHighlight by
    animateColorAsState(if (completed && highlight != Color.Transparent) highlight.copy(alpha = 0.1f) else highlight)
    val textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
    val textColor by animateColorAsState(
        MaterialTheme.colorScheme.onPrimaryContainer.run { if (completed) copy(alpha = 0.3f) else this }
    )
    Surface(
        color = adjustedHighlight,
        modifier = Modifier.padding(4.dp).height(34.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        var active by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .onPointerEvent(PointerEventType.Enter) { active = true }
            .onPointerEvent(PointerEventType.Exit) { active = false }) {
            BasicTextField(
                name,
                enabled = !completed,
                singleLine = true,
                onValueChange = onNameChange,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodyLarge
                    .copy(
                        color = textColor,
                        textDecoration = textDecoration,
                    ),
                decorationBox = { innerTextField ->
                    Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) { innerTextField() }
                },
                modifier = Modifier.weight(1f, true)
//                    .padding(start = 16.dp)
                    .fillMaxSize()
                    .onKeyEvent { event ->
                        if (event.isCtrlPressed && event.key == Key.E && event.type == KeyEventType.KeyDown) {
                            onTab()
                            true
                        } else false
                    }
            )
//            AnimatedVisibility(active, enter = fadeIn(tween(50)), exit = fadeOut(tween(50))) {
            if (active) {
                IconButton(
                    onClick = { completed = !completed },
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
