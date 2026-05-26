package me.dvyy.tasks.tasks.ui.elements.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.Cursors
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.tasks.ui.elements.helpers.optional

@Composable
fun Divider(
    orientation: Orientation = Orientation.Vertical,
    modifier: Modifier = Modifier,
    toggleable: Boolean = false,
    onToggle: (expanded: Boolean) -> Unit = {},
    applyHoverCursor: Boolean = true,
) {
    val ver = orientation == Orientation.Vertical
    var expanded by remember { mutableStateOf(true) }
    val padding = if (toggleable) 32.dp else 0.dp
    val clickable = if (toggleable) Modifier.clickable {
        expanded = !expanded
        onToggle(expanded)
    } else Modifier
    val surface = DividerDefaults.color
    val hover = MaterialTheme.colorScheme.primary
    var color by remember { mutableStateOf(surface) }
    val icon = if (ver) Cursors.horizontalResize else Cursors.verticalResize

    Box(
        modifier
            .optional(applyHoverCursor) { pointerHoverIcon(icon) }
            .onHoverIfAvailable(onEnter = { color = hover }, onExit = { color = surface })
            .sizeIn(minWidth = 8.dp, minHeight = 8.dp)
            .then(clickable),
    ) {
        if (toggleable) Box(Modifier.align(Alignment.TopEnd)) {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Icon(Icons.Rounded.ArrowDropDown, "Toggle", modifier = Modifier.rotate(rotation))
        }
        Box(Modifier.align(Alignment.Center)) {
            if (ver) HorizontalDivider(Modifier.padding(end = padding), color = Color.Transparent)
            else VerticalDivider(Modifier.padding(top = padding), color = Color.Transparent)
        }
    }
}
