package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.getBestTextColor

data class BoxButtonProps(
    val verticalPadding: Dp? = null,
    val horizontalPadding: Dp? = null,
)

val LocalBoxButtonProps = compositionLocalOf { BoxButtonProps() }

@Composable
fun BoxButton(
    onClick: () -> Unit,
    color: Color = Color.Transparent,
    contentColor: Color = color.getBestTextColor(),
    shape: Shape = UI.shapes.rounded,
    border: BorderStroke? = null,//BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
    modifier: Modifier = Modifier.Companion,
    properties: BoxButtonProps = LocalBoxButtonProps.current,
    content: @Composable () -> Unit,
) {
    val hPad = properties.horizontalPadding ?: UI.padding.sm
    val vPad = properties.verticalPadding ?: UI.padding.sm
    Surface(
        modifier = modifier
            .width(UI.tasks.propertyButtonSize - UI.padding.sm * 2 + hPad * 2)
            .height(UI.tasks.propertyButtonSize - UI.padding.sm * 2 + vPad * 2)
            .padding(
                horizontal = properties.horizontalPadding ?: UI.padding.sm,
                vertical = properties.verticalPadding ?: UI.padding.sm,
            ),
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
}


@Composable
fun ButtonRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) =
    Row(
        modifier.padding(horizontal = UI.padding.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)
    ) {
        CompositionLocalProvider(LocalBoxButtonProps provides BoxButtonProps(horizontalPadding = 0.dp)) {
            content()
        }
    }

@Composable
fun ButtonColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) =
    Column(
        modifier.padding(vertical = UI.padding.sm),
        verticalArrangement = Arrangement.spacedBy(UI.padding.sm)
    ) {
        CompositionLocalProvider(LocalBoxButtonProps provides BoxButtonProps(verticalPadding = 0.dp)) {
            content()
        }
    }