package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DropTarget(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit,
    layoutViewModel: LayoutViewModel = koinViewModel(),
) = Box {
    fun update(select: LayoutStructure, structure: LayoutStructure) {
        onLayoutUpdate(structure)
        layoutViewModel.setActiveLayout(select)
    }

    HoverBox(
        Modifier.fillMaxSize(),
        hoverableModifier = Modifier.fillMaxSize(0.33f),
        onDropped = { new ->
            val tabbed = structure.withTab(new)
            update(tabbed, tabbed)
        })

    Column(Modifier.fillMaxSize()) {
        HoverBox(Modifier.weight(1f), onDropped = { new ->
            val newTab  = LayoutStructure.Tabbed(listOf(new))
            update(newTab, LayoutStructure.Split(newTab, structure, orientation = Orientation.Vertical))
        })
        Spacer(Modifier.weight(1f))
        HoverBox(Modifier.weight(1f), onDropped = { new ->
            val newTab  = LayoutStructure.Tabbed(listOf(new))
            update(newTab, LayoutStructure.Split(structure, newTab, orientation = Orientation.Vertical))
        })
    }
    Row(Modifier.fillMaxSize()) {
        HoverBox(Modifier.weight(1f), onDropped = { new ->
            val newTab  = LayoutStructure.Tabbed(listOf(new))
            update(newTab, LayoutStructure.Split(newTab, structure, orientation = Orientation.Horizontal))
        })
        Spacer(Modifier.weight(1f))
        HoverBox(Modifier.weight(1f), onDropped = { new ->
            val newTab  = LayoutStructure.Tabbed(listOf(new))
            update(newTab, LayoutStructure.Split(structure, newTab, orientation = Orientation.Horizontal))
        })
    }
}
