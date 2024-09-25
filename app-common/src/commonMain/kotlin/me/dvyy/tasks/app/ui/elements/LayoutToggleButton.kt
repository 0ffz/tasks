package me.dvyy.tasks.app.ui.elements

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import me.dvyy.tasks.layout.ui.LayoutButton

@Composable
fun LayoutToggleButton(
    button: LayoutButton,
    enabled: Boolean,
    onClick: (Boolean) -> Unit = {},
) {
    IconToggleButton(checked = enabled, onCheckedChange = onClick) {
        Icon(button.icon, button.displayName)
    }
}
