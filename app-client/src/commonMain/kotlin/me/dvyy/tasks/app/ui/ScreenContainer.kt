package me.dvyy.tasks.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.ChevronLeft
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.ui.elements.TopBarContainer
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.settings.ui.RowOrBox
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.optional

@Composable
fun ScreenContainer(
    title: String,
    extraItems: @Composable RowScope.() -> Unit = {},
    utilityPane: (@Composable (setExpanded: (Boolean) -> Unit) -> Unit)? = null,
    utilityPaneText: String? = null,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val ui = UI
    var expanded by remember { mutableStateOf(true) }
    RowOrBox(!ui.isSmall) {
        AnimatedVisibility(
            !ui.isSmall || expanded,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            utilityPane?.let {
                Surface(shape = UI.shapes.rounded, tonalElevation = if (ui.isSmall) UI.elevation.lv0 else 0.5f.dp) {
                    Column(Modifier.optional(!ui.isSmall) { sizeIn(maxWidth = 300.dp) }.fillMaxHeight()) {
                        TopBarContainer {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                                Spacer(Modifier.weight(1f))
                                Text(utilityPaneText ?: "", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.weight(1f))
                                if (ui.isSmall) BoxButton(
                                    TablerIcons.Outlined.X,
                                    onClick = onClose,
                                    "Close"
                                )
                            }
                        }
                        it({ expanded = it })
                    }
                }
            }
        }
        TintedVerticalDivider()
        AnimatedVisibility(
            !ui.isSmall || !expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                TopBarContainer {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (ui.isSmall) {
                            BoxButton(
                                TablerIcons.Outlined.ChevronLeft,
                                onClick = { expanded = !expanded },
                                "Open menu"
                            )
                        }
                        extraItems()
                        Spacer(Modifier.weight(1f))
                        Text(title, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        BoxButton(
                            TablerIcons.Outlined.X,
                            onClick = onClose,
                            "Close"
                        )
                    }
                }
                Column {
                    content()
                }
            }
        }
    }
}