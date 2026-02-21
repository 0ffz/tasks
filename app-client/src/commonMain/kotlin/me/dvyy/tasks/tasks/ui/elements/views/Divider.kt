package me.dvyy.tasks.tasks.ui.elements.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.Cursors
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.tasks.ui.elements.helpers.optional

@Composable
fun Divider(
    orientation: Orientation = Orientation.Vertical,
    toggleable: Boolean = false,
    onToggle: (expanded: Boolean) -> Unit = {},
    applyHoverCursor: Boolean = true,
    modifier: Modifier = Modifier,
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
            .then(clickable),
    ) {
        if (toggleable) Box(Modifier.align(Alignment.TopEnd)) {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Icon(Icons.Rounded.ArrowDropDown, "Toggle", modifier = Modifier.rotate(rotation))
        }
        Box(
            Modifier.align(Alignment.Center)
        ) {
            val animatedColor by animateColorAsState(color)
            if (ver) HorizontalDivider(Modifier.padding(end = padding), color = animatedColor)
            else VerticalDivider(Modifier.padding(top = padding), color = animatedColor)
        }
    }
}
