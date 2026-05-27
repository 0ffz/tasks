package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Plus
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.state.ProjectHeaderState
import me.dvyy.tasks.utils.CachedUpdate

@Composable
fun ProjectHeader(
    header: ProjectHeaderState,
    listId: ListId,
    colored: Boolean,
    //TODO get this passed down from a parent since BoxWithConstraints causes issues with IntrinsicSizeK
    width: Dp = 500.dp,
    loading: Boolean = false,
    showDivider: Boolean = true,
    addTask: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) = Column {
    val color = when {
        colored -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val colorFaded = color.fade(alpha = 0.6f)
    Row(
        modifier,//padding(horizontal = pd.md, vertical = pd.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val monthTextStyle = when {
//                width < 140.dp -> MaterialTheme.typography.titleLargeEmphasized
//                width < 180.dp -> MaterialTheme.typography.headlineSmallEmphasized
            else -> MaterialTheme.typography.headlineMedium
        }
        when (header) {
            is ProjectHeaderState.Date -> {
                val weekdayTextStyle = when {
//                        width < 140.dp -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.headlineSmall
                }
                val date = header.date
                val text = when {
                    width < 140.dp -> "${date.day}"
//                        width < 160.dp -> "${date.month.name.take(1)}${date.day}"
                    else -> "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)} ${date.day}"
                }
                if (width > 100.dp) Text(
                    text,
                    Modifier.weight(1f, true),
                    style = monthTextStyle,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                )
                Text(
                    date.dayOfWeek.name.lowercase().capitalize().take(3),
                    style = weekdayTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = (if (width > 100.dp) Modifier else Modifier.weight(1f, true)),
                    color = colorFaded
                )
            }

            is ProjectHeaderState.Named -> {
                CachedUpdate(listId, header.displayName, header.onRename) { name, setName ->
                    BasicTextField(
                        name,
                        enabled = header.canRename,
                        onValueChange = { setName(it) },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.weight(1f, true),
                        textStyle = monthTextStyle.copy(
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
        if (addTask != null) BoxButton(
            TablerIcons.Outlined.Plus,
            onClick = addTask,
            "Add task to top",
            tint = colorFaded,
        )
    }

    // == Loading indicator and divider
    if (showDivider) {
        if (!loading) HorizontalDivider(
            thickness = 2.dp,
            color = color
        )
        AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
        }
    }
}
