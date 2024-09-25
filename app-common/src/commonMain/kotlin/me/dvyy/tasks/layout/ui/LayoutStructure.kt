package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

sealed interface SplitAmount {
    data class Fixed(val value: Dp) : SplitAmount
    data class Percent(val value: Float) : SplitAmount
}
sealed interface LayoutStructure {
    data class Scrollable(
        val views: List<LayoutStructure>,
        val orientation: Orientation,
    ) : LayoutStructure

    data class Split(
        val first: LayoutStructure,
        val second: LayoutStructure,
        val split: SplitAmount = SplitAmount.Percent(0.5f),
        val orientation: Orientation,
        val firstEnabled: Boolean = true,
        val secondEnabled: Boolean = true,
    ) : LayoutStructure

    data class Single(val content: @Composable () -> Unit) : LayoutStructure

    data class Tabbed(
        val tabs: List<Tab>,
        val selected: Int,
        val name: String? = null,
    ) : LayoutStructure

    data class Tab(val name: String, val content: LayoutStructure)

    data object Empty : LayoutStructure
}
