package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class LayoutViewModel : ViewModel() {
    val leftSidebar = MutableStateFlow<LayoutStructure>(LayoutStructure.Empty)
    val rightSidebar = MutableStateFlow<LayoutStructure>(LayoutStructure.Empty)
    val bottomBar = MutableStateFlow<LayoutStructure>(LayoutStructure.Empty)
    val mainView = MutableStateFlow<LayoutStructure>(LayoutStructure.Empty)
    val layoutButtonLocations = MutableStateFlow(LayoutButtonLocations())

    val activeContentView get() = mainView

    init {
        layoutButtonLocations.update {
            LayoutButtonLocations(
                left = listOf(LayoutButtons.fileTree),
                bottom = listOf(LayoutButtons.projects),
            )
        }
    }

    val mobileLayout = combine(mainView, bottomBar) { main, bottom ->
        LayoutStructure.Split(
            first = main,
            second = bottom,
            orientation = Orientation.Vertical,
            secondEnabled = bottom != LayoutStructure.Empty,
        )
    }
    val desktopLayout = combine(leftSidebar, rightSidebar, bottomBar, mainView) { left, right, bottom, main ->
        LayoutStructure.Split(
            first = LayoutStructure.Split(
                first = left,
                second = main,
                orientation = Orientation.Horizontal,
                firstEnabled = left != LayoutStructure.Empty,
            ),
            second = bottom,
            orientation = Orientation.Vertical,
            secondEnabled = bottom != LayoutStructure.Empty,
        )
    }
}
