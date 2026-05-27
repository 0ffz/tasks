package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Check
import dev.seyfarth.tablericons.outlined.Highlight
import dev.seyfarth.tablericons.outlined.Trash
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import me.dvyy.tasks.tasks.ui.state.TaskState
import me.dvyy.tasks.time.TimeViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel

/**
 * Options for modifying a task.
 * Shown below the textbox, contains buttons for moving date, deleting, etc...
 */
@Composable
fun TaskOptions(
    task: TaskState,
    submitAction: (() -> Unit)? = null,
) {
    val time: TimeViewModel by rememberViewModel()
    LocalUIState.current
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
                    Icon(TablerIcons.Outlined.Highlight, contentDescription = "Tag")
                }

                // == Date picker button
                val today by time.today.collectAsState()
                //TODO decide whether we'd prefer to open on task's current date or today
                TaskDatePicker(today, onChangeDate = { task.mutate.moveTo(it) })

                Spacer(Modifier.weight(1f))

                // == Submit button on right hand side (for widget)
                if (submitAction != null) {
                    FilledIconButton(onClick = submitAction) {
                        Icon(TablerIcons.Outlined.Check, contentDescription = "Submit")
                    }
                } else {
                    BoxButton(AppIcons.Trash, onClick = { task.mutate.onDelete() }, "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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


