package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import me.dvyy.tasks.app.ui.UI

@Composable
fun BoxButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RoundedCornerShape(UI.size.sm),
    border: BorderStroke? = null,//BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
    modifier: Modifier = Modifier.Companion,
    content: @Composable () -> Unit,
) = Surface(
    modifier = modifier.size(UI.tasks.propertyButtonSize).padding(UI.padding.sm),
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