package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.Cursors
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI

@Composable
fun DividerPill(orientation: Orientation = Orientation.Vertical) {
    val ui = LocalUIState.current
    val hor = orientation == Orientation.Vertical
    val icon = if (hor) Cursors.horizontalResize else Cursors.verticalResize
    fun Modifier.horWidth(amount: Dp) = if (hor) width(amount) else height(amount)
    fun Modifier.horHeight(amount: Dp) = if (hor) height(amount) else width(amount)

    Box(
        Modifier.horHeight(ui.dividerHeight).pointerHoverIcon(icon),
        contentAlignment = Alignment.Center
    ) {
        if (hor) HorizontalDivider() else VerticalDivider()
        Surface(Modifier.horHeight(8.dp).horWidth(220.dp)) {}
        Surface(
            shape = MaterialTheme.shapes.small,
            tonalElevation = UI.elevation.lv1,
            modifier = Modifier.horHeight(8.dp).horWidth(200.dp)
        ) { }
    }
}
