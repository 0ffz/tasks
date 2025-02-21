package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.tree.ui.FileStructure

// The scope used here is the scope that is used for the mapping work
fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M,
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)

class LayoutViewModel(
    prefs: LocalPreferencesRepository,
) : ViewModel() {
    private val _leftSidebar =
        prefs.serializable<LayoutStructure.Single>(viewModelScope, "leftSidebar", LayoutButtons.fileTree.structure)
    val leftSidebar = _leftSidebar.asStateFlow()
    val mobileLeftSidebar = _leftSidebar.map(viewModelScope) {
        it.takeIf { it != LayoutStructure.Empty } ?: LayoutButtons.fileTree.structure
    }

    val rightSidebar = prefs.serializable<LayoutStructure>(viewModelScope, "rightSidebar", LayoutStructure.Empty)
    val bottomBar = prefs.serializable<LayoutStructure>(viewModelScope, "bottomBar", LayoutStructure.Empty)
    private val _mainView = prefs.serializable<LayoutStructure.Tabbed>(viewModelScope, "mainView", LayoutStructure.Tabbed(listOf(), 0))

    val mainView: StateFlow<LayoutStructure.Tabbed> = _mainView.map(viewModelScope) {
        it
//        it.takeIf { it is LayoutStructure.Tabbed }
//            ?: LayoutStructure.Tabbed(listOf(), 0)
    }

    val layoutButtonLocations = MutableStateFlow(LayoutButtonLocations())
    val topRightLayout = mainView.map(viewModelScope) {
//        var top = it
//        while (top is LayoutStructure.Split) {
//            top = if (top.orientation == Orientation.Vertical) top.first else top.second
//        }
//        top
        it
    }
    val topLeftLayout = mainView.map(viewModelScope) {
//        var top = it
//        while (top is LayoutStructure.Split) {
//            top = if (top.orientation == Orientation.Vertical) top.first else top.first
//        }
//        top
        it
    }

    fun findTopRow(layout: LayoutStructure): List<LayoutStructure> = when (layout) {
        is LayoutStructure.Split -> {
            if (layout.orientation == Orientation.Horizontal) {
                listOf(layout.first) + findTopRow(layout.second)
            } else {
                findTopRow(layout.first)
            }
        }

        else -> listOf(layout)
    }

    private val _activeLayout = MutableStateFlow<LayoutStructure?>(null)

    val activeLayout: StateFlow<LayoutStructure> = combine(_activeLayout, mainView) { active, main ->
        active.takeIf { it != LayoutStructure.Empty } ?: main
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mainView.value)

    private val openFilesChannel = Channel<FileStructure.File>()
    val openFilesFlow = openFilesChannel.receiveAsFlow()

    init {
        layoutButtonLocations.update {
            LayoutButtonLocations(
                left = listOf(LayoutButtons.fileTree),
//                bottom = listOf(LayoutButtons.projects),
            )
        }
    }

    fun openInActiveView(file: FileStructure.File) {
        openFilesChannel.trySend(file)
    }

    fun openInActiveView(note: VaultPath) {
        openInActiveView(FileStructure.File(LayoutStructure.Single.Project(note)))
    }

    fun setActiveLayout(layout: LayoutStructure) {
//        println("Old layout was ${_activeLayout.value}")
//        println("Setting layout to $layout")
        _activeLayout.update { layout }
    }

    fun setTabView(layout: LayoutStructure.Tabbed) {
        _mainView.update { layout }
    }

    fun setLeftSidebar(layout: LayoutStructure.Single) {
        _leftSidebar.update { layout }
    }

    val mobileLayout = combine(mainView, bottomBar) { main, bottom ->
        LayoutStructure.Split(
            first = main,
            second = bottom,
            orientation = Orientation.Vertical,
            secondEnabled = bottom != LayoutStructure.Empty,
            mergeWhenEmpty = false,
        )
    }
    val desktopLayout = combine(leftSidebar, rightSidebar, bottomBar, mainView) { left, right, bottom, main ->
        LayoutStructure.Split(
            first = LayoutStructure.Split(
                first = LayoutStructure.Tabbed(
                    listOf(LayoutStructure.History(left.wrap {
                        androidx.compose.material3.Surface(
                            tonalElevation = UI.elevation.lv1,
                            modifier = Modifier.fillMaxSize()
                        ) { it() }
                    })),
                    fullWidth = true,
                    selectable = false,
                ),
                second = main,
                split = SplitAmount.Fixed(200.dp),
                orientation = Orientation.Horizontal,
                firstEnabled = left != LayoutStructure.Empty,
                mergeWhenEmpty = false,
            ),
            second = bottom,
            orientation = Orientation.Vertical,
            mergeWhenEmpty = false,
            secondEnabled = bottom != LayoutStructure.Empty,
        )
    }

    val topRow = desktopLayout
        .map { findTopRow(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
