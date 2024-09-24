package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.AppViewButtons

class LayoutViewModel : ViewModel() {
    val leftSidebar = MutableStateFlow<ViewStructure>(ViewStructure.Empty)
    val rightSidebar = MutableStateFlow<ViewStructure>(ViewStructure.Empty)
    val bottomBar = MutableStateFlow<ViewStructure>(ViewStructure.Empty)
    val mainView = MutableStateFlow<ViewStructure>(ViewStructure.Empty)
    val viewButtons = MutableStateFlow(ViewButtons())

    init {
        viewButtons.update {
            ViewButtons(
                left = listOf(AppViewButtons.fileTree),
                bottom = listOf(AppViewButtons.projects),
            )
        }
    }

    val structure = combine(leftSidebar, rightSidebar, bottomBar, mainView) { left, right, bottom, main ->
        ViewStructure.Split(
//            first = ViewStructure.Split(
            first = ViewStructure.Split(
                first = left,
                second = main,
                orientation = Orientation.Horizontal,
                firstEnabled = left != ViewStructure.Empty,
            ),
//                second = right,
//                orientation = Orientation.Horizontal,
//                secondEnabled = right != ViewStructure.Empty,
//            ),
            second = bottom,
            orientation = Orientation.Vertical,
            secondEnabled = bottom != ViewStructure.Empty,
        )
    }
}


data class ViewButton(
    val displayName: String,
    val id: String,
    val structure: ViewStructure,
    val icon: ImageVector,
)

class ViewButtons(
    val left: List<ViewButton> = emptyList(),
    val right: List<ViewButton> = emptyList(),
    val bottom: List<ViewButton> = emptyList(),
)
