package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.layouts.*
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton

@Composable
fun Layout(
    structure: LayoutStructure,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
) {
//    if(structure is LayoutStructure.Tabbed || structure == LayoutStructure.Empty) {
//        DropTarget(structure, onLayoutUpdate)
//    }
    when (structure) {
        is LayoutStructure.Scrollable -> ScrollableLayout(structure)

        is LayoutStructure.Split -> SplitLayout(structure, onLayoutUpdate)

        is LayoutStructure.Tabbed -> TabbedLayout(structure, onLayoutUpdate)

        is LayoutStructure.Single -> SingleLayout(structure, onLayoutUpdate)

        LayoutStructure.Empty -> Box(Modifier.fillMaxSize())
    }
}

@Composable
fun SingleLayout(
    structure: LayoutStructure.Single,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
) = Box {
    Scaffold(
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
                            onClose = { onLayoutUpdate(LayoutStructure.Empty) },
                            onDropLayout = onLayoutUpdate,
                        )
//                        WeekViewActions()
                        structure.trailingOptions()
                        BoxButton(onClick = { onLayoutUpdate(LayoutStructure.Empty) }) {
                            Icon(AppIcons.Close, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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