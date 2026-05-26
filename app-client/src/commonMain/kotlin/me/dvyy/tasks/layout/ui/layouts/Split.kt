package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.ColumnOrRow
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.tasks.ui.elements.views.Divider

@Composable
fun Split(
    splitAmount: SplitAmount,
    onSplitAmountChange: (SplitAmount) -> Unit,
    firstEnabled: Boolean = true,
    secondEnabled: Boolean = true,
    orientation: Orientation = Orientation.Horizontal,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    if (!secondEnabled && !firstEnabled) return
    if (!secondEnabled) Box { first() }
    if (!firstEnabled) Box { second() }
    BoxWithConstraints {
        var splitAmount by remember { mutableStateOf<SplitAmount>(splitAmount) }
        val density = LocalDensity.current
        val isHorizontal = orientation == Orientation.Horizontal

        val draggableState = rememberDraggableState { delta ->
            val totalSize = with(density) { (if (isHorizontal) maxWidth else maxHeight).toPx() }
            if (totalSize <= 0) return@rememberDraggableState

            splitAmount = when (val split = splitAmount) {
                is SplitAmount.Fixed -> SplitAmount.Fixed(split.value + with(density) { delta.toDp() })
                is SplitAmount.Percent -> SplitAmount.Percent((split.value + delta / totalSize).coerceIn(0f, 1f))
            }
            onSplitAmountChange(splitAmount)
        }
        val max = splitAmount.toDp(if (isHorizontal) maxWidth else maxHeight)
        val mod = if (isHorizontal) Modifier.width(max) else Modifier.height(max)
        ColumnOrRow(orientation) {
            val size = UI.shapes.roundedCornerSize
            Box(mod.clip(RoundedCornerShape(bottomStart = if (isHorizontal) 0.dp else size, bottomEnd = size, topEnd = if (isHorizontal) size else 0.dp))) { first() }

            Divider(orientation, Modifier.draggable(draggableState, orientation))

            Box(
                Modifier.fillMaxSize().clip(
                    RoundedCornerShape(topStart = size, topEnd = if (isHorizontal) 0.dp else size, bottomStart = if (isHorizontal) size else 0.dp)
                )
            ) { second() }
        }
    }
}