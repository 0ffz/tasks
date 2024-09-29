package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.Highlight

@Composable
fun TaskSelectedSurface(
    visible: Boolean,
    highlight: Highlight,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f)
    val color =  MaterialTheme.colorScheme.surface.copy(alpha = alpha)
    val fullCornerSize = 20.dp
    val cornerShape by animateDpAsState(if (visible) fullCornerSize else 0.dp)
    val padding by animateDpAsState(if (visible) UI.padding.lg else 0.dp)
    val highlightColor = highlight.color
        .copy(alpha = 0.15f)
        .takeIf { visible && highlight != Highlight.Unmarked } ?: Color.Transparent
    val animatedHighlight by animateColorAsState(highlightColor)
    Surface(
        modifier = modifier.padding(vertical = padding),
        shape = RoundedCornerShape(cornerShape),
        color = color,
        tonalElevation = UI.elevation.lv1,
    ) {
        Surface(
            color = animatedHighlight,
            shape = RoundedCornerShape(fullCornerSize),
        ) {
            content()
        }
    }
}
