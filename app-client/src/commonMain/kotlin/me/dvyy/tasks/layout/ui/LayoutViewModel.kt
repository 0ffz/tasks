package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.tree.ui.FileStructure
import me.dvyy.tasks.utils.combinedStateFlow

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
        it.takeIf { it != LayoutStructure.Remove } ?: LayoutButtons.fileTree.structure
    }

    val rightSidebar = prefs.serializable<LayoutStructure>(viewModelScope, "rightSidebar", LayoutStructure.Remove)
    val bottomBar = MutableStateFlow<LayoutStructure>(LayoutStructure.Remove)//prefs.serializable<LayoutStructure>(viewModelScope, "bottomBar", LayoutStructure.Remove)

    //    val tabs = prefs.serializable<LayoutStructure.Tabbed>(viewModelScope, "tabs", LayoutStructure.Tabbed(emptyList()))
    val tabs = MutableStateFlow<List<LayoutStructure>>(listOf(LayoutStructure.Empty))
    val selectedTab = MutableStateFlow(0)
    val activeLayout = viewModelScope.combinedStateFlow(selectedTab, tabs) { selected, tabs ->
        tabs.getOrNull(selected) ?: LayoutStructure.Empty
    }
//    val mainView = tabs.map { it.tabs.getOrNull(it.selected) ?: it.tabs.firstOrNull() ?: LayoutStructure.Empty }
//        .stateIn(viewModelScope, SharingStarted.Lazily, LayoutStructure.Empty)

    val layoutButtonLocations = MutableStateFlow(LayoutButtonLocations())

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

//    val activeLayout: StateFlow<LayoutStructure> = combine(_activeLayout, mainView) { active, main ->
//        active.takeIf { it != LayoutStructure.Remove } ?: main
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mainView.value)

    private val openFilesChannel = Channel<FileStructure.File>()
    val openFilesFlow = openFilesChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            openFilesFlow.collectLatest { (content) ->
                setActiveLayout(content)
            }
        }
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

    fun setActiveLayout(layout: LayoutStructure) {
        tabs.update { currentTabs ->
            val index = selectedTab.value
            if (index in currentTabs.indices) {
                currentTabs.toMutableList().apply { set(index, layout) }
            } else {
                currentTabs + layout
            }
        }
    }

    fun switchTab(index: Int) = selectedTab.update { index }

    fun closeTab(index: Int) = tabs.update {
        if (index in it.indices) it.toMutableList().apply { removeAt(index) } else it
    }

    fun openTab(layout: LayoutStructure): Int {
        tabs.update { it.plus(layout) }
        return tabs.value.lastIndex + 1
    }


//    fun replaceActive(layout: LayoutStructure) {
//        tabs.update {
//            val replace = it.tabs.getOrNull(it.selected) is LayoutStructure.Single
//            it.withTab(layout, atIndex = it.selected, replace = replace)
//        }
//    }

//    fun setMainView(layout: LayoutStructure) {
//        tabs.update {
//            when (layout) {
//                is LayoutStructure.Tabbed -> layout
//                is LayoutStructure.Remove -> LayoutStructure.Tabbed(listOf())
//                else -> it
//            }
//        }
//    }

    fun setLeftSidebar(layout: LayoutStructure.Single) {
        _leftSidebar.update { layout }
    }

//    val mobileLayout = combine(mainView, bottomBar) { main, bottom ->
//        LayoutStructure.Split(
//            first = main,
//            second = bottom,
//            orientation = Orientation.Vertical,
//            secondEnabled = bottom != LayoutStructure.Remove,
//            mergeWhenEmpty = false,
//        )
//    }
//    val desktopLayout = combine(leftSidebar, rightSidebar, bottomBar, mainView) { left, right, bottom, main ->
//        LayoutStructure.Split(
//            first = LayoutStructure.Split(
//                first = LayoutStructure.Tabbed(
//                    listOf(left),
//                    fullWidth = true,
//                    selectable = false,
//                ).wrap {
//                    androidx.compose.material3.Surface(
//                        tonalElevation = UI.elevation.lv1,
//                        modifier = Modifier.fillMaxSize()
//                    ) { it() }
//                },
//                second = main,
//                split = SplitAmount.Fixed(200.dp),
//                orientation = Orientation.Horizontal,
//                firstEnabled = left != LayoutStructure.Remove,
//                mergeWhenEmpty = false,
//            ),
//            second = bottom,
//            orientation = Orientation.Vertical,
//            mergeWhenEmpty = false,
//            secondEnabled = bottom != LayoutStructure.Remove,
//        )
//    }

//    val topRow = desktopLayout
//        .map { findTopRow(it) }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
