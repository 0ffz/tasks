package me.dvyy.tasks.tasks.ui.elements.helpers.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.PlatformSpecifics
import me.dvyy.tasks.core.ui.getBestTextColor

data class BoxButtonProps(
    val verticalPadding: Dp? = null,
    val horizontalPadding: Dp? = null,
    val innerPadding: Dp? = null,
)

val LocalBoxButtonProps = compositionLocalOf { BoxButtonProps() }

@Composable
fun BoxButtonContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    tint: Color = LocalContentColor.current,
    spacedBy: Dp = UI.padding.sm,
    content: @Composable RowScope.() -> Unit,
) {
    val size = LocalPropertyButtonSizeProvider.current
    val localProps = LocalBoxButtonProps.current
    val vPad = localProps.verticalPadding ?: UI.padding.sm
    val hPad = localProps.horizontalPadding ?: UI.padding.sm
    Box(modifier.padding(vertical = vPad, horizontal = hPad), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .clip(UI.shapes.rounded)//.background(MaterialTheme.colorScheme.surfaceColorAtElevation(currElevation + elevation))
                .clickable { onClick() }
                .height(size - UI.padding.sm * 2)
                .background(color)
                .padding(horizontal = 5.dp),
//                    .border(
//                        BorderStroke(3.dp, if(elevation == 0.dp) Color.Transparent else MaterialTheme.colorScheme.surfaceColorAtElevation(currElevation + elevation + 3.dp)),
//                        UI.shapes.rounded
//                    )
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacedBy)
        ) {
            CompositionLocalProvider(
                LocalPropertyButtonSizeProvider provides (size - 2 * UI.padding.sm),
                LocalBoxButtonProps provides BoxButtonProps(innerPadding = 4.dp, horizontalPadding = 0.dp),
                LocalContentColor provides tint,
            ) {
                content()
            }
        }
    }
}

val LocalPropertyButtonSizeProvider = compositionLocalOf { PlatformSpecifics.minHitSize }

@Composable
fun BoxButton(
    icon: ImageVector,
    onClick: () -> Unit,
    tooltip: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    tint: Color = color.getBestTextColor(),
    shape: Shape = UI.shapes.rounded,
    border: BorderStroke? = null,//BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
    properties: BoxButtonProps = LocalBoxButtonProps.current,
    contentAlignment: Alignment = Alignment.Center,
) {
    val hPad = properties.horizontalPadding ?: UI.padding.sm
    val vPad = properties.verticalPadding ?: UI.padding.sm
    val buttonSize = LocalPropertyButtonSizeProvider.current
    Surface(
        modifier = modifier
            .width(buttonSize - UI.padding.sm * 2 + hPad * 2)
            .height(buttonSize - UI.padding.sm * 2 + vPad * 2)
            .padding(
                horizontal = properties.horizontalPadding ?: UI.padding.sm,
                vertical = properties.verticalPadding ?: UI.padding.sm,
            ),
        onClick = onClick,
        color = color,
        shape = shape,
        border = border,
        contentColor = tint,
    ) {
        val innerPadding = properties.innerPadding ?: PlatformSpecifics.paddingInnerSize
        Box(contentAlignment = contentAlignment, modifier = Modifier.padding(innerPadding)) {
//            if (tooltip == null) Icon(icon, tooltip)
            TooltipBox(
                //TODO should be placed outside of padded box so tooltip shows on full hover area?
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                tooltip = { PlainTooltip { Text(tooltip) } },
                state = rememberTooltipState(),
            ) {
                Icon(icon, tooltip)
            }
        }
    }
}


@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = UI.padding.sm,
    spacedBy: Dp = UI.padding.sm,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier.padding(horizontal = horizontalPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(spacedBy)
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