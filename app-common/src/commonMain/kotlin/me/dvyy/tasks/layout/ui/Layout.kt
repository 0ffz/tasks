package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.layout.ui.layouts.DropTarget
import me.dvyy.tasks.layout.ui.layouts.ScrollableLayout
import me.dvyy.tasks.layout.ui.layouts.SplitLayout
import me.dvyy.tasks.layout.ui.layouts.TabbedLayout
import me.dvyy.tasks.notes.ui.PageTopBar

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

        is LayoutStructure.Single -> {
            Column {
                PageTopBar(
                    text = { Text(structure.text) },
                    buttons = {
                        structure.topButtons()
                        IconButton(onClick = { onLayoutUpdate(LayoutStructure.Empty) }) {
                            Icon(AppIcons.Close, "Close split tab")
                        }
                    }
                )
                structure.content()
            }
        }

        LayoutStructure.Empty -> Box(Modifier.fillMaxSize())
    }
    if (structure is LayoutStructure.Single || structure == LayoutStructure.Empty) {
        DropTarget(structure, onLayoutUpdate)
    }
}



