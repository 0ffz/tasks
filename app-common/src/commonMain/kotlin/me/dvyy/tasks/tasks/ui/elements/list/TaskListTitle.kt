package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.state.Loadable
import me.dvyy.tasks.app.ui.state.loadedOrNull
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.CachedUpdate

@Composable
fun TaskListTitle(
    props: Loadable<TaskListProperties>,
    colored: Boolean,
    interactions: TaskListInteractions? = null,
    loading: Boolean = false,
    showDivider: Boolean = true,
) {
    val color =
        if (colored) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        Modifier.padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        val loadedProps = props.loadedOrNull() ?: return
        CachedUpdate(loadedProps, interactions?.onPropertiesChanged ?: {}) { (props, setProps) ->
            if (props.date != null) {
                val date = props.date!!
                Text(
                    "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}",
                    Modifier.weight(1f, true),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                )
                Text(
                    date.dayOfWeek.name.lowercase().capitalize().take(3),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    color = color.copy(alpha = 0.6f)
                )
            } else {
                BasicTextField(
                    props.displayName ?: "Untitled",
                    onValueChange = { setProps(props.copy(displayName = it)) },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.weight(1f, true),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
    if (showDivider) Box {
        val isLoading = loading || props is Loadable.Loading

        if (!isLoading) HorizontalDivider(
            thickness = 2.dp,
            color = color
        )
        AnimatedVisibility(isLoading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
        }
    }
}
