package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.runtime.Composable
import me.dvyy.tasks.layout.ui.LayoutStructure

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
        is LayoutStructure.Empty -> EmptyLayout(onLayoutUpdate)
        is LayoutStructure.Single -> SingleLayout(structure, onLayoutUpdate)
        is LayoutStructure.Wrap -> structure.wrap { Layout(structure.child, onLayoutUpdate) }
    }
}

