package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.CachedUpdate
import me.dvyy.tasks.utils.Loadable
import me.dvyy.tasks.utils.loadedOrNull
import java.time.format.TextStyle
import java.util.*

@Composable
fun TaskListTitle(
    props: Loadable<TaskListProperties>,
    colored: Boolean,
    interactions: TaskListInteractions? = null,
    loading: Boolean = false,
    showDivider: Boolean = true,
    key: VaultPath,
    modifier: Modifier = Modifier,
) {
    val color =
        if (colored) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface
    val colorFaded = color.fade(alpha = 0.6f)
    Row(
        modifier.padding(start = UI.padding.md, top = UI.padding.sm, bottom = UI.padding.sm, end = UI.padding.sm),
        verticalAlignment = Alignment.Bottom,
    ) {
        val loadedProps = props.loadedOrNull() ?: return
        CachedUpdate(key, loadedProps, interactions?.onPropertiesChanged ?: {}) { props, setProps ->
            if (props.date != null) {
                val date = props.date!!
                Text(
                    "${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).lowercase().capitalize()} ${date.dayOfMonth}",
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
                    color = colorFaded
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
            IconButton(onClick = {
                interactions?.createNewTask?.invoke(false)
            }, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add task to top",
                    tint = colorFaded,
                )
            }
        }
    }
    if (showDivider) Box(Modifier.padding(horizontal = UI.padding.md)) {
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
