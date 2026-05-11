package me.dvyy.tasks.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun LeadingIcon(icon: ImageVector?, contentDescription: String, content: @Composable (() -> Unit)) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon
            != null
        ) {
            Icon(icon, contentDescription = contentDescription)
            Spacer(Modifier.width(8.dp))
        }
        content()
    }

@Composable
fun LeadingIcon(icon: @Composable () -> Unit, content: @Composable () -> Unit) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(8.dp))
        content()
    }
