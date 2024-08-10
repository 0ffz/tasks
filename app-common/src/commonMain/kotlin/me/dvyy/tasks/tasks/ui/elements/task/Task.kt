package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDate
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
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
