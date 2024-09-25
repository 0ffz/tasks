package me.dvyy.tasks.layout.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.tasks.ui.elements.list.Divider
import me.dvyy.tasks.tasks.ui.elements.list.thenOptional

@Composable
fun Views(structure: ViewStructure) {
    val ui = LocalUIState.current

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
            val hor = structure.orientation == Orientation.Horizontal
            var size by remember { mutableStateOf(0) }
            var splitPercent by remember { mutableStateOf(0.5f) }
            var dividerCoordinates by remember { mutableStateOf<Offset?>(null) }//mutableStateOf<LayoutCoordinates?>(null) }
            val density = LocalDensity.current

            val splitPercentCoerced =/* if (!structure.secondEnabled) 0.5f else*/ splitPercent.coerceIn(0.05f, 0.95f)
            Box(Modifier.onGloballyPositioned {
                size = if (hor) it.size.width else it.size.height
            }) {

                ColumnOrRow(structure.orientation) {
                    if (structure.firstEnabled) Box(
                        (with(density) {
                            if (structure.orientation == Orientation.Vertical)
                                Modifier.thenOptional(structure.secondEnabled) { height(size.toDp() * splitPercentCoerced) }
                            else Modifier.thenOptional(structure.secondEnabled) { width(size.toDp() * splitPercentCoerced) }
                        })
                            .onGloballyPositioned { offset ->
                                //if(offset.positionInParent() != Offset.Zero) dividerCoordinates = offset
                                dividerCoordinates = offset.positionInParent() +
                                        if (structure.orientation == Orientation.Vertical)
                                            Offset(0f, offset.size.height.toFloat())
                                        else Offset(offset.size.width.toFloat(), 0f)
                            }
                    ) {
                        Views(structure.first)
                    }

//                    if (structure.firstEnabled && structure.secondEnabled)
//                        Spacer(Modifier.size(1.dp))

                    if (structure.secondEnabled) Views(structure.second)
                }
//                return
                val offset = dividerCoordinates ?: return@Box
                val padding = if (ui.isSingleColumn) 17.dp else 9.dp

                val scrollableState = rememberScrollableState { delta ->
                    val newSplitHeight = (splitPercent + delta / size).coerceIn(0f, 1f)
                    splitPercent = newSplitHeight
                    if (newSplitHeight == 0f || newSplitHeight == 1f) 0f
                    else delta
                }
                val draggableState = rememberDraggableState { delta ->
                    splitPercent = (splitPercent + delta / size).coerceIn(0f, 1f)
                }

                val handleModifier = Modifier
                    .scrollable(scrollableState, structure.orientation)
                    .draggable(draggableState, structure.orientation)

                if (structure.firstEnabled && structure.secondEnabled) with(density) {
                    Box(
                        Modifier
                            .run {
                                if (structure.orientation == Orientation.Horizontal) width(padding).fillMaxHeight()
                                else height(padding).fillMaxWidth()
                            }
                            .offset(offset.x.toDp() - padding / 2, offset.y.toDp() - padding / 2)
                            .then(handleModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Divider(structure.orientation, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        is ViewStructure.Single -> Box(Modifier.fillMaxSize()) { structure.content() }
        is ViewStructure.Tabbed -> {
            Column {
                Surface(Modifier.fillMaxWidth(), tonalElevation = 1.dp) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        structure.name?.let {
                            Box(Modifier.padding(6.dp)) {
                                Text(it, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        structure.tabs.forEachIndexed { index, tab ->
                            Box(Modifier.clickable {  }.width(IntrinsicSize.Max)) {
                                Box(Modifier.padding(6.dp)) {
                                    Text(
                                        tab.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                    )
                                }
                                if (index == structure.selected) Surface(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter),
                                    color = MaterialTheme.colorScheme.primary,
                                ) { }
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.alpha(0.6f))
                structure.tabs.getOrNull(structure.selected)?.content?.let { Views(it) }
            }
        }

        ViewStructure.Empty -> {
            Box(Modifier.fillMaxSize())
        }
    }
}
