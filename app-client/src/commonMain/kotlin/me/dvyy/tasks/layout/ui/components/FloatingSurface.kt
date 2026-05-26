package me.dvyy.tasks.layout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI

@Composable
fun FloatingSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = UI.shapes.roundedExtra,
        tonalElevation = UI.elevation.lv2,
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv3)
        )
    ) {
        content()
    }
}