package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.rounded.BorderColor
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import me.dvyy.tasks.tasks.ui.state.TaskState
import me.dvyy.tasks.time.TimeViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Options for modifying a task.
 * Shown below the textbox, contains buttons for moving date, deleting, etc...
 */
@Composable
fun TaskOptions(
    task: TaskState,
    submitAction: (() -> Unit)? = null,
    time: TimeViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    var focused: FocusedOption by remember { mutableStateOf(FocusedOption.None) }
    fun toggleFocused() {
        focused = if (focused == FocusedOption.Highlight) FocusedOption.None else FocusedOption.Highlight
    }
    Column {
        Box(Modifier.height(UI.tasks.propertyButtonSize)) {
            ButtonRow(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            ) {
                // == Button that opens dropdown to select task highlight
                HighlightButton(
                    task.uiState.highlight,
                    onClick = { toggleFocused() }
                ) {
                    Icon(Icons.Rounded.BorderColor, contentDescription = "Tag", Modifier.size(18.dp))
                }

                // == Date picker button
                val today by time.today.collectAsState()
                //TODO decide whether we'd prefer to open on task's current date or today
                TaskDatePicker(today, onChangeDate = { task.mutate.moveTo(it) })

                Spacer(Modifier.weight(1f))

                // == Submit button on right hand side (for widget)
                if (submitAction != null) {
                    FilledIconButton(onClick = submitAction) {
                        Icon(Icons.Outlined.Done, contentDescription = "Submit")
                    }
                } else {
                    BoxButton(
                        onClick = { task.mutate.onDelete() },
//                        shape = RoundedCornerShape(bottomEnd = 16.dp),
//                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {

                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", Modifier.size(18.dp))
                    }
//                    FilledTonalIconButton(
//
//                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//                        modifier = Modifier.height(UI.tasks.checkboxSize)
//                    ) {
//                    }
                }
            }
        }

        // == Dropdown for task highlight choice
        AnimatedVisibility(
            focused == FocusedOption.Highlight,
            enter = expandVertically(tween(150)),
            exit = shrinkVertically(tween(150))
        ) {
            HighlightButtons(
                onSelectHighlight = { new -> task.updateUi { it.copy(highlight = new) } },
                ::toggleFocused,
                Modifier.fillMaxWidth()//.horizontalScroll(rememberScrollState())
            )
        }
    }
}


