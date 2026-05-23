package me.dvyy.tasks.core.ui.components

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun DesktopVerticalScrollbar(state: ScrollableState, modifier: Modifier)

@Composable
expect fun DesktopHorizontalScrollbar(state: ScrollableState, modifier: Modifier)