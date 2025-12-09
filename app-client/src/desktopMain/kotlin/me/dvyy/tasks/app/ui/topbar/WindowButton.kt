package me.dvyy.tasks.app.ui.topbar

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import me.dvyy.tasks.app.ui.UI

@Composable
fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxHeight().width(UI.size.xxl),
        contentColor = MaterialTheme.colorScheme.onSurface,
        color = Color.Transparent
    ) {
        Icon(icon, "", Modifier.padding(UI.padding.md))
    }
}
