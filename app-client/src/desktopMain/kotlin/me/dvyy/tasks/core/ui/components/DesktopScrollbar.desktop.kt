package me.dvyy.tasks.core.ui.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun DesktopVerticalScrollbar(
    state: ScrollableState,
    modifier: Modifier,
) {
    val adapter = when (state) {
        is ScrollState -> rememberScrollbarAdapter(state)
        is LazyListState -> rememberScrollbarAdapter(state)
        else -> return
    }
    VerticalScrollbar(adapter, modifier)
}

@Composable
actual fun DesktopHorizontalScrollbar(state: ScrollableState, modifier: Modifier) {
    val adapter = when (state) {
        is ScrollState -> rememberScrollbarAdapter(state)
        is LazyListState -> rememberScrollbarAdapter(state)
        else -> return
    }
    HorizontalScrollbar(adapter, modifier)
}