package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.layouts.ScrollableLayout
import me.dvyy.tasks.layout.ui.layouts.SplitLayout
import me.dvyy.tasks.layout.ui.layouts.TabbedLayout

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

        is LayoutStructure.Single -> structure.content()

        LayoutStructure.Empty -> Box(Modifier.fillMaxSize())
    }
}
