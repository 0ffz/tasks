package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.components.DropTarget

@Composable
fun CalculateLayout(
    index: Int,
    layout: LayoutDefinition,
    onLayoutChange: (LayoutDefinition) -> Unit,
    modifier: Modifier = Modifier,
): Unit = Box(modifier) {
    if (index > layout.operations.lastIndex) return@Box
    when (val current = layout.operations[index]) {
        is LayoutOperation.Split -> {
            val first = index + 1
            var count = 1
            var i = index + 1
            while (i < layout.operations.size) {
                if (layout.operations[i] is LayoutOperation.Split) count++
                else count--
                i++
                if (count == 0) break
            }
            val second = i
            Split(
                orientation = current.orientation,
                splitAmount = current.amount,
                onSplitAmountChange = {},
                first = { CalculateLayout(first, layout, onLayoutChange) },
                second = { CalculateLayout(second, layout, onLayoutChange) }
            )
        }

        is LayoutOperation.Place -> {
            //TODO reuse screens when order changes to cache state on layout changes.
            ScreenLayout(current.destination, layout, index, onLayoutChange)
            DropTarget(index, layout, onLayoutChange, current.destination)
        }
    }
}
