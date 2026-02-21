package me.dvyy.tasks.tasks.ui.elements.helpers

import androidx.compose.foundation.gestures.Orientation

operator fun Orientation.not() = if (this == Orientation.Vertical) Orientation.Horizontal else Orientation.Vertical
