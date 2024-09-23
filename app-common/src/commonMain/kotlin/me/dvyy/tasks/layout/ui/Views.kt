package me.dvyy.tasks.layout.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.tasks.ui.elements.list.Divider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Views(structure: ViewStructure) {
    when (structure) {
        is ViewStructure.Scrollable -> {
            val scrollState = rememberScrollState()
            val hor = structure.orientation == Orientation.Horizontal
            val scrollModifier =
                if (hor) Modifier.horizontalScroll(scrollState)
                else Modifier.verticalScroll(scrollState)

            ColumnOrRow(
                structure.orientation,
                Modifier.fillMaxSize().then(scrollModifier)
            ) {
                structure.views.forEach {
                    var isExpanded by remember { mutableStateOf(true) }
                    Divider(
                        structure.orientation,
                        toggleable = true,
                        applyHoverCursor = false,
                        onToggle = { isExpanded = it })
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = if (hor) expandHorizontally() else expandVertically(),
                        exit = if (hor) shrinkHorizontally() else shrinkVertically()
                    ) {
                        Views(it)
                    }
                }
            }
        }

        is ViewStructure.Split -> {
            var size by remember { mutableStateOf(0) }
            var splitPercent by remember { mutableStateOf(0.5f) }
            var dividerCoordinates by remember { mutableStateOf<Offset?>(null) }//mutableStateOf<LayoutCoordinates?>(null) }
//            var dividerSize by remember { mutableStateOf(IntSize.Zero) }
            Box(Modifier.onGloballyPositioned {
                size = if (structure.orientation == Orientation.Vertical) it.size.height else it.size.width
            }) {

                val scrollableState = rememberScrollableState { delta ->
                    val newSplitHeight = (splitPercent + delta / size).coerceIn(0f, 1f)
                    splitPercent = newSplitHeight
                    if (newSplitHeight == 0f || newSplitHeight == 1f) 0f
                    else delta
                }
                val draggableState = rememberDraggableState { delta ->
                    splitPercent = (splitPercent + delta / size).coerceIn(0f, 1f)
                }
                val handleModifier = /*if (ui.isSingleColumn) {
                        Modifier.scrollable(
                            scrollableState,
                            Orientation.Vertical,
                        )
                    } else */Modifier.draggable(draggableState, structure.orientation)

                ColumnOrRow(structure.orientation) {
                    if (structure.firstEnabled) Box(
                        (if (structure.orientation == Orientation.Vertical) Modifier.height(size.dp * splitPercent)
                        else Modifier.width(size.dp * splitPercent))
                            .onGloballyPositioned { offset ->
                                //if(offset.positionInParent() != Offset.Zero) dividerCoordinates = offset
                                dividerCoordinates = offset.positionInParent() +
                                        if (structure.orientation == Orientation.Vertical) Offset(
                                            0f,
                                            offset.size.height.toFloat()
                                        )
                                        else Offset(offset.size.width.toFloat(), 0f)
                            }
                    ) {
                        Views(structure.first)
                    }

                    if (structure.firstEnabled) Box(
                        Modifier.then(handleModifier)
//                        .onSizeChanged { dividerSize = it }
                    ) {
                        Spacer(Modifier.size(1.dp))
//                        Divider(structure.orientation)
                    }
                    Views(structure.second)
                }
                val offset = dividerCoordinates ?: return@Box
                val padding = 9.dp
                println(offset)

                Box(
                    Modifier
                        .run {
                            if (structure.orientation == Orientation.Horizontal) width(padding).fillMaxHeight()
                            else height(padding).fillMaxWidth()
                        }
                        .offset(offset.x.dp - padding / 2, offset.y.dp - padding / 2)
                        .then(handleModifier),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(structure.orientation, modifier = Modifier.fillMaxSize())
                }
            }
        }

        is ViewStructure.Single -> structure.content()
        is ViewStructure.Tabbed -> {
            Column {
                Surface(Modifier.fillMaxWidth(), tonalElevation = 0.dp) {
                    Row {
                        structure.tabs.forEachIndexed { index, tab ->
                            Surface(
                                Modifier.clickable {/* structure.selected = index */ },
                                tonalElevation = if (index == structure.selected) 0.dp else 0.dp
                            ) {
                                Box(Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) {
                                    Text(tab.name, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.alpha(0.6f))
                structure.tabs.getOrNull(structure.selected)?.content?.let { Views(it) }
            }
        }
    }
}
