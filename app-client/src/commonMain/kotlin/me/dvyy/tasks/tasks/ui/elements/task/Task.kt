package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.elements.task.properties.TaskOptions
import me.dvyy.tasks.tasks.ui.elements.task.text.TaskCheckBox
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ui.horizontalTaskTextPadding),
                    modifier = Modifier.padding(start = ui.horizontalTaskTextPadding),
                ) {
//                    Text("🔥", modifier = Modifier.zIndex(0f).zIndex(1f).drawBehind {
////                        drawRoundRect(Color(0xFF404040), cornerRadius = CornerRadius(8.dp.toPx()), topLeft = Offset(-2.dp.toPx(), -1.dp.toPx()), size = size.copy(width = size.width + 4.dp.toPx(), height = size.height + 2.dp.toPx()))
//                    })
//                    if(task.uiState.text.hashCode() % 4 == 0)
//                        Text("❗")
                    Box(Modifier.zIndex(0f).weight(1f, true), contentAlignment = Alignment.CenterStart) {
                        TaskTextField(task, focusRequested, modifier = Modifier.taskHighlight(task.uiState.highlight, task.selected, task.uiState.completed))
                    }
                    val responsive = LocalUIState.current

                    if (forceShowCheckbox || responsive.alwaysShowCheckbox || isHovered || task.selected)
                        TaskCheckBox(task, icon = overrideCheckboxIcon)
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

@Composable
fun Modifier.taskHighlight(highlight: Highlight, selected: Boolean, completed: Boolean) = composed {
    val adjustedHighlight by animateColorAsState(highlight.color.fade(if (completed) UI.tasks.completedFade else 1f))
    drawWithCache {
        onDrawBehind {
            if (!selected)
                drawRoundRect(adjustedHighlight, cornerRadius = CornerRadius(8.dp.toPx()), topLeft = Offset(-3.dp.toPx(), -1.dp.toPx()), size = size.copy(width = size.width + 6.dp.toPx(), height = size.height + 2.dp.toPx()))
        }

    }
}