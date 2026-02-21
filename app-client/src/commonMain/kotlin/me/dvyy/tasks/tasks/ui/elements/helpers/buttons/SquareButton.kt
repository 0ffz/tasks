package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.getBestTextColor

@Composable
fun SquareButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier.Companion,
    content: @Composable () -> Unit = {},
) {
    LocalUIState.current
    BoxButton(
        onClick = onClick,
//        borderShape = RoundedCornerShape(bottomStart = 16.dp),
        color = color,
        contentColor = color.getBestTextColor(),
        modifier = Modifier
            .focusProperties { canFocus = false }
    ) { content() }
}