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
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.state.ProjectHeaderState
import me.dvyy.tasks.utils.CachedUpdate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectHeader(
    header: ProjectHeaderState,
    listId: ListId,
    colored: Boolean,
    loading: Boolean = false,
    showDivider: Boolean = true,
    addTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pd = UI.padding
    val color = when {
        colored -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val colorFaded = color.fade(alpha = 0.6f)
    Row(
        modifier.padding(horizontal = pd.md, vertical = pd.sm),
        verticalAlignment = Alignment.Bottom,
    ) {
        when (header) {
            is ProjectHeaderState.Date -> {
                val date = header.date
                Text(
                    "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)} ${date.day}",
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

            }

            is ProjectHeaderState.Named -> {
                CachedUpdate(listId, header.displayName, header.onRename) { name, setName ->
                    BasicTextField(
                        name,
                        onValueChange = { setName(it) },
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

            else -> {
                //TODO show loading progress indicator
            }
        }
        // == Add task to top button
        IconButton(onClick = addTask, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add task to top",
                tint = colorFaded,
            )
        }
    }

    // == Loading indicator and divider
    if (showDivider) Box(Modifier.padding(horizontal = pd.md)) {
        if (!loading) HorizontalDivider(
            thickness = 2.dp,
            color = color
        )
        AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
        }
    }
}
