package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.core.ui.components.ColumnOrRow
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.tasks.ui.elements.list.Divider
import me.dvyy.tasks.tasks.ui.elements.list.optional
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@Composable
fun SplitLayout(
    structure: LayoutStructure.Split,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) {
    val ui = LocalUIState.current
    val hor = structure.orientation == Orientation.Horizontal
    var size by remember { mutableStateOf(0) }
    var splitAmount by remember { mutableStateOf(structure.split) }
    var dividerCoordinates by remember { mutableStateOf<Offset?>(null) }//mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(structure) {
        snapshotFlow { splitAmount }.debounce(2.seconds).collectLatest {
            onLayoutUpdate(structure.copy(split = it))
        }
    }
//            val splitPercentCoerced =/* if (!structure.secondEnabled) 0.5f else*/ splitPercent.coerceIn(0.05f, 0.95f)
    Box(Modifier.onGloballyPositioned {
        size = if (hor) it.size.width else it.size.height
    }) {
        ColumnOrRow(structure.orientation) {
            if (structure.firstEnabled) Box(
                (with(density) {
                    val splitSize = when (val split = splitAmount) {
                        is SplitAmount.Percent -> size.toDp() * split.value
                        is SplitAmount.Fixed -> split.value
                    }

                    if (structure.orientation == Orientation.Vertical)
                        Modifier.optional(structure.secondEnabled) { height(splitSize) }
                    else Modifier.optional(structure.secondEnabled) { width(splitSize) }
                })
                    .onGloballyPositioned { offset ->
                        //if(offset.positionInParent() != Offset.Zero) dividerCoordinates = offset
                        dividerCoordinates = offset.positionInParent() +
                                if (structure.orientation == Orientation.Vertical)
                                    Offset(0f, offset.size.height.toFloat())
                                else Offset(offset.size.width.toFloat(), 0f)
                    }
            ) {
                Layout(
                    structure.first,
                    onLayoutUpdate = {
                        onLayoutUpdate(
                            if (structure.mergeWhenEmpty && it == LayoutStructure.Empty) {
                                layoutViewModel.setActiveLayout(structure.second)
                                structure.second
                            } else structure.copy(first = it)
                        )
                    }
                )
            }

            if (structure.secondEnabled) Layout(
                structure.second,
                onLayoutUpdate = {
                    onLayoutUpdate(
                        if (structure.mergeWhenEmpty && it == LayoutStructure.Empty) {
                            layoutViewModel.setActiveLayout(structure.first)
                            structure.first
                        } else structure.copy(second = it)
                    )
                }
            )
        }

        val offset = dividerCoordinates ?: return@Box
        val padding = if (ui.isSmall) 17.dp else 9.dp

        val scrollableState = rememberScrollableState { delta ->
            val split = splitAmount

            if (split is SplitAmount.Fixed) splitAmount =
                SplitAmount.Fixed(split.value + with(density) { delta.toDp() })
            else if (split is SplitAmount.Percent) splitAmount =
                SplitAmount.Percent((split.value + delta / size).coerceIn(0f, 1f))
//                    splitPercent = newSplitHeight
//                    if (newSplitHeight == 0f || newSplitHeight == 1f) 0f
            /*else*/ delta
        }
        val draggableState = rememberDraggableState { delta ->
            val split = splitAmount
            if (split is SplitAmount.Fixed) splitAmount =
                SplitAmount.Fixed(split.value + with(density) { delta.toDp() })
            else if (split is SplitAmount.Percent) splitAmount =
                SplitAmount.Percent((split.value + delta / size).coerceIn(0f, 1f))
//                    splitPercent = (splitPercent + delta / size).coerceIn(0f, 1f)
        }

        val handleModifier = Modifier
            .scrollable(scrollableState, structure.orientation)
            .draggable(draggableState, structure.orientation)

        if (structure.firstEnabled && structure.secondEnabled) with(density) {
            Box(
                Modifier
                    .run {
                        if (hor) width(padding).fillMaxHeight()
                            .offset(offset.x.toDp() - padding / 2, offset.y.toDp())
                        else height(padding).fillMaxWidth()
                            .offset(offset.x.toDp(), offset.y.toDp() - padding / 2)
                    }
                    .then(handleModifier),
                contentAlignment = Alignment.Center
            ) {
                Divider(structure.orientation, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
