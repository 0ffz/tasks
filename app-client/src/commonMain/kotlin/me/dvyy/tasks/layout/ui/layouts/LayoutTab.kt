package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.screens.builder.Screen
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButtonContainer
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.LocalBoxButtonProps

@Composable
fun LayoutTab(
    selected: Boolean,
    definition: LayoutDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingOptions: @Composable () -> Unit = {},
) {
    val elevation = if (selected) 5.dp else 0.dp
    val currElevation = LocalAbsoluteTonalElevation.current
    Surface(modifier) {
        BoxButtonContainer(
            onClick,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(currElevation + elevation)
//                Modifier
//                    .weight(1f)
//                    .clip(UI.shapes.rounded).background(MaterialTheme.colorScheme.surfaceColorAtElevation(currElevation + elevation))
//                    .clickable { onClick() }
//                    .height(UI.tabHeight - UI.padding.sm * 2)
////                    .border(
////                        BorderStroke(3.dp, if(elevation == 0.dp) Color.Transparent else MaterialTheme.colorScheme.surfaceColorAtElevation(currElevation + elevation + 3.dp)),
////                        UI.shapes.rounded
////                    )
//                    .padding(horizontal = UI.padding.sm),
        ) {
            definition.placeOperations.forEach {
                val screen = it.destination.toScreen()
                ScreenTab(
                    screen, Modifier.weight(1f)
                        // fading edge
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    0.75f to Color.White,
                                    1f to Color.Transparent,
                                    startX = 0f,
                                    endX = size.width,
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        })
            }
            trailingOptions()
        }
    }
}

@Composable
fun ScreenTab(
    screen: Screen,
    modifier: Modifier = Modifier,
) {
    LocalBoxButtonProps.current.innerPadding
    LeadingIcon(modifier, { Icon(screen.icon, "Icon", Modifier.size(20.dp)) }) {
        Text(screen.tabLabel(), softWrap = false)
    }
}


// TODO merge with ScreenTab
@Composable
private fun TabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .clickableWithoutRipple(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                tonalElevation = UI.elevation.lv1,
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonRow {
                    Spacer(Modifier.width(UI.padding.sm))
                    Row(Modifier.weight(1f)) {
                        title()
                    }
                    BoxButton(
                        TablerIcons.Outlined.X,
                        onClick = { onClose() },
                        "Close",
                    )
                }
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )

                Surface {
                    Box(Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
        }
    }
}