package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.LayoutButton
import me.dvyy.tasks.tasks.ui.elements.helpers.optional

@Composable
fun LayoutToggleButton(
    button: LayoutButton,
    enabled: Boolean,
    onClick: (Boolean) -> Unit = {},
) {
    IconToggleButton(
        checked = enabled,
        onCheckedChange = onClick,
        shape = MaterialTheme.shapes.small
    ) {
        Box(
            modifier = Modifier
                .optional(enabled) { background(MaterialTheme.colorScheme.primaryContainer) }
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                button.icon,
                button.displayName,
                tint = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current
            )
        }
    }
}
