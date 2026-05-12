package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.tasks.ui.elements.task.properties.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskCheckBox
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskHighlight
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskTextField
import me.dvyy.tasks.tasks.ui.state.TaskState

@Composable
fun Task(
    task: TaskState,
    focusRequested: Boolean = false,
    forceShowCheckbox: Boolean = false,
    overrideCheckboxIcon: ImageVector? = null,
    overrideCheckboxCompletedIcon: ImageVector? = null,
) {
    var isHovered by remember { mutableStateOf(false) }
    val ui = LocalUIState.current
    val selectedState by rememberUpdatedState(task.selected)
    LaunchedEffect(task.uiState) {
        snapshotFlow { selectedState }
            .drop(1)
            .filter { !it } // Listen to deselect
            .collect {
//                UiLogger.i { "Deselcted $task" }
                if (task.uiState.text.isEmpty()) task.mutate.onDelete()
            }
    }

    Box(
        modifier = Modifier
            .onHoverIfAvailable(
                onEnter = { isHovered = true },
                onExit = { isHovered = false }
            )
            .heightIn(min = ui.tasks.height)
            .focusProperties { canFocus = false }
            .clickableWithoutRipple { task.mutate.onSelect() } // Consume click so deselect doesn't get called
            .onPreviewKeyEvent { task.mutate.onKeyEvent(it, task.uiState) }
    ) {
        TaskSelectedSurface(task.selected, task.uiState.highlight) {
            Column {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(start = ui.horizontalTaskTextPadding),
                ) {
                    Box(Modifier.weight(1f, true), contentAlignment = Alignment.CenterStart) {
                        if (!task.selected) TaskHighlight(task.uiState)
                        TaskTextField(task, focusRequested)
                    }
                    val responsive = LocalUIState.current

                    if (forceShowCheckbox || responsive.alwaysShowCheckbox || isHovered || task.selected)
                        TaskCheckBox(task, icon = overrideCheckboxIcon, completedIcon = overrideCheckboxCompletedIcon)
                }
                AnimatedVisibility(
                    task.selected,
                    enter = fadeIn(tween(delayMillis = 100)) + expandVertically(),
                    exit = fadeOut(tween(durationMillis = 100)) + shrinkVertically(),
                    modifier = Modifier.disableDragGestures()
                ) {
                    TaskOptions(task)
                }
            }
        }
    }
}
