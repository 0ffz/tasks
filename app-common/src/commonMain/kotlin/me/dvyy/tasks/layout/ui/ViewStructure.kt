package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable

sealed interface ViewStructure {
    data class Scrollable(
        val views: List<ViewStructure>,
        val orientation: Orientation,
    ) : ViewStructure

    data class Split(
        val first: ViewStructure,
        val second: ViewStructure,
        val split: Float = 0.5f,
        val orientation: Orientation,
        val firstEnabled: Boolean = true,
    ) : ViewStructure

    data class Single(val content: @Composable () -> Unit) : ViewStructure

    data class Tabbed(
        val tabs: List<Tab>,
        val selected: Int,
    ) : ViewStructure

    data class Tab(val name: String, val content: ViewStructure)
}
