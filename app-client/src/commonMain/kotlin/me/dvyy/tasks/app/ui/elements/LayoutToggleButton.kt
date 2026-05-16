package me.dvyy.tasks.app.ui.elements

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.layout.ui.LayoutButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton

@Composable
fun LayoutToggleButton(
    button: LayoutButton,
    enabled: Boolean,
    onClick: () -> Unit = {},
) {
    val color = if (enabled) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    BoxButton(
        onClick = onClick,
        color = color
    ) {
        Icon(
            button.icon,
            button.displayName,
            tint = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current
        )
    }
}
