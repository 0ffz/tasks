package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.app.ui.LocalUIState

@Composable
fun CircleButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier.Companion,
    content: @Composable () -> Unit = {},
) {
    val ui = LocalUIState.current
    OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = color,
        ),
        onClick = onClick,
        modifier = modifier.size(ui.tasks.height).focusProperties { canFocus = false }
//            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), CircleShape),
//        border = border,
    ) { content() }
}