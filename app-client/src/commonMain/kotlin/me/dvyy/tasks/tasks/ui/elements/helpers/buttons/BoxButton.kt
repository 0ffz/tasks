package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.getBestTextColor

@Composable
fun BoxButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    contentColor: Color = color.getBestTextColor(),
    shape: Shape = UI.shapes.rounded,
    border: BorderStroke? = null,//BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
    modifier: Modifier = Modifier.Companion,
    padded: Boolean = true,
    content: @Composable () -> Unit,
) = Surface(
    modifier = modifier.size(UI.tasks.propertyButtonSize).padding(if (padded) UI.padding.sm else 0.dp),
    onClick = onClick,
    color = color,
    shape = shape,
    border = border,
    contentColor = contentColor,
) {
    Box(contentAlignment = Alignment.Center) {
        content()
    }
}