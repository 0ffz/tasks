package me.dvyy.tasks.layout.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.LayoutOperation
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import kotlin.uuid.Uuid

@Composable
fun DropTarget(
    index: Int,
    layout: LayoutDefinition,
    onLayoutChange: (LayoutDefinition) -> Unit,
    structure: ScreenDest,
    splitTargets: Boolean = true,
) = Box {
    fun update(orientation: Orientation, select: ScreenDest, structure: ScreenDest, closeSource: Uuid?) {
        if (layout.indexOf(closeSource) == index) return
        onLayoutChange(layout.replace(index, LayoutOperation.Split(orientation = orientation), select, structure).minus(closeSource))
    }

    HoverBox(
        Modifier.fillMaxSize(),
        hoverableModifier = {
            Modifier.align(Alignment.Center).fillMaxSize(if (splitTargets) 0.33f else 1f)
        },
        onDrop = { new ->
//            val tabbed = structure.withTab(new)
            onLayoutChange(layout.replace(index, new.destination).replace(layout.indexOf(new.source), structure))
        })

    if (!splitTargets) return@Box

    Column(Modifier.fillMaxSize()) {
        HoverBox(
            Modifier.weight(1f),
            onDrop = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
                val newTab = new
                update(
                    Orientation.Vertical,
                    newTab.destination,
                    structure,
                    newTab.source,
                )
            },
            hoverableModifier = {
                Modifier.align(Alignment.TopCenter).fillMaxWidth().fillMaxHeight(0.75f)
            })
        HoverBox(
            Modifier.weight(1f),
            onDrop = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
                val newTab = new
                update(
                    Orientation.Vertical,
                    structure,
                    newTab.destination,
                    newTab.source,
                )
            },
            hoverableModifier = {
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().fillMaxHeight(0.75f)
            })
    }
    Row(Modifier.fillMaxSize()) {
        HoverBox(
            Modifier.weight(1f),
            onDrop = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
                val newTab = new
                update(
                    Orientation.Horizontal,
                    newTab.destination,
                    structure,
                    newTab.source,
                )
            },
            hoverableModifier = {
                Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth(0.75f)
            })
        HoverBox(
            Modifier.weight(1f),
            onDrop = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
                val newTab = new
                update(
                    Orientation.Horizontal,
                    structure,
                    newTab.destination,
                    newTab.source,
                )
            },
            hoverableModifier = {
                Modifier.align(Alignment.CenterEnd).fillMaxHeight().fillMaxWidth(0.75f)
            })
    }
//    Row(Modifier.fillMaxSize()) {
//        HoverBox(Modifier.weight(1f), onDropped = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
//            update(newTab, LayoutStructure.Split(newTab, structure, orientation = Orientation.Horizontal))
//        })
//        Spacer(Modifier.weight(1f))
//        HoverBox(Modifier.weight(1f), onDropped = { new ->
//            val newTab = LayoutStructure.Tabbed(listOf(new))
//            update(newTab, LayoutStructure.Split(structure, newTab, orientation = Orientation.Horizontal))
//        })
//    }
}
