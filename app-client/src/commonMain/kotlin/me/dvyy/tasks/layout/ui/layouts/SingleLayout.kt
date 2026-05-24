package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow

@Composable
fun SingleLayout(
    structure: LayoutStructure.Single,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
) = Box {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            if (structure.showsTopBar) Surface(tonalElevation = 0.5.dp) {
                Column(Modifier.height(UI.tabHeight)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(UI.padding.md))
                        LayoutTab(
                            structure,
                            Modifier.weight(1f),
                            showCloseButton = false,
                            selected = false,
                            onClose = { onLayoutUpdate(LayoutStructure.Remove) },
                            onDropLayout = onLayoutUpdate,
                        )
//                        WeekViewActions()
                        ButtonRow {
                            structure.trailingOptions()
                            BoxButton(onClick = { onLayoutUpdate(LayoutStructure.Empty) }) {
                                Icon(TablerIcons.Outlined.X, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            structure.cachedContent()
        }
    }
    if (structure.hasDropTargets) DropTarget(structure, onLayoutUpdate)
}