package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
inline fun ColumnOrRow(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (orientation == Orientation.Vertical) Column(modifier) { content() }
    else Row(modifier) { content() }
}
