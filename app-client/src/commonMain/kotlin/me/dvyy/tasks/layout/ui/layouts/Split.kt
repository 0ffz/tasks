package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.tasks.ui.elements.views.Divider
import kotlin.math.roundToInt

@Composable
fun Split(
    splitAmount: SplitAmount = SplitAmount.Percent(0.5f),
    onSplitAmountChange: (SplitAmount) -> Unit,
    firstEnabled: Boolean = true,
    secondEnabled: Boolean = true,
    orientation: Orientation = Orientation.Horizontal,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    var totalSizePx by remember { mutableIntStateOf(0) }
    var splitAmount by remember { mutableStateOf<SplitAmount>(splitAmount) }
    val density = LocalDensity.current
    val isHorizontal = orientation == Orientation.Horizontal

    val draggableState = rememberDraggableState { delta ->
        if (totalSizePx <= 0) return@rememberDraggableState

//        onSplitAmountChange(when (val split = splitAmount) {
//            is SplitAmount.Fixed -> SplitAmount.Fixed(split.value + with(density) { delta.toDp() })
//            is SplitAmount.Percent -> SplitAmount.Percent((split.value + delta / totalSizePx).coerceIn(0f, 1f))
//        })
        splitAmount = when (val split = splitAmount) {
            is SplitAmount.Fixed -> SplitAmount.Fixed(split.value + with(density) { delta.toDp() })
            is SplitAmount.Percent -> SplitAmount.Percent((split.value + delta / totalSizePx).coerceIn(0f, 1f))
        }
        onSplitAmountChange(splitAmount)
    }

    Layout(
        content = {
            Box { first() }
            Divider(orientation, Modifier.draggable(draggableState, orientation))
            Box { second() }
        }
    ) { measurables, constraints ->
        // Capture the size locally for layout calculations and update the state for the drag handler
        val currentTotalSizePx = if (isHorizontal) constraints.maxWidth else constraints.maxHeight
        totalSizePx = currentTotalSizePx

        // 1. Destructure the measurables for instant readability
        val (firstMeasurable, dividerMeasurable, secondMeasurable) = measurables
        val hitTargetPaddingPx = 8.dp.roundToPx()

        val splitPx = when (val split = splitAmount) {
            is SplitAmount.Percent -> currentTotalSizePx * split.value
            is SplitAmount.Fixed -> split.value.toPx()
        }.coerceIn(0f, currentTotalSizePx.toFloat()).roundToInt()

        // Helper to dynamically adjust max width/height constraints based on orientation
        fun Constraints.restrictCross(maxSize: Int) = copy(minWidth = 0, minHeight = 0).let {
            if (isHorizontal) it.copy(maxWidth = maxSize) else it.copy(maxHeight = maxSize)
        }

        // 2. Measure First
        val firstMax = if (!secondEnabled) currentTotalSizePx else splitPx
        val firstPlaceable = firstMeasurable
            .takeIf { firstEnabled }
            ?.measure(constraints.restrictCross(firstMax))

        val firstSpan = if (isHorizontal) (firstPlaceable?.width ?: 0) else (firstPlaceable?.height ?: 0)

        // 3. Measure Second
        val secondMax = (currentTotalSizePx - firstSpan).coerceAtLeast(0)
        val secondPlaceable = secondMeasurable
            .takeIf { secondEnabled }
            ?.measure(constraints.restrictCross(secondMax))

        // 4. Measure Divider
        val dividerConstraints = constraints.copy(
            minWidth = if (isHorizontal) hitTargetPaddingPx else constraints.minWidth,
            minHeight = if (!isHorizontal) hitTargetPaddingPx else constraints.minHeight
        )
        val dividerPlaceable = dividerMeasurable.measure(dividerConstraints)

        // 5. Layout and placement
        layout(constraints.maxWidth, constraints.maxHeight) {
            firstPlaceable?.placeRelative(0, 0)

            val divOffset = firstSpan - (hitTargetPaddingPx / 2)

            if (isHorizontal) {
                secondPlaceable?.placeRelative(x = firstSpan, y = 0)
                if (firstEnabled && secondEnabled) dividerPlaceable.placeRelative(x = divOffset, y = 0)
            } else {
                secondPlaceable?.placeRelative(x = 0, y = firstSpan)
                if (firstEnabled && secondEnabled) dividerPlaceable.placeRelative(x = 0, y = divOffset)
            }
        }
    }
}