package me.dvyy.tasks.layout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.tree.ui.FileStructure
import me.dvyy.tasks.utils.combinedStateFlow

class LayoutViewModel : ViewModel() {
//    private val _leftSidebar =
//        prefs.serializable<LayoutStructure.Single>(viewModelScope, "leftSidebar", LayoutButtons.fileTree.structure)
//    val leftSidebar = _leftSidebar.asStateFlow()
//    val mobileLeftSidebar = _leftSidebar.map(viewModelScope) {
//        it.takeIf { it != LayoutStructure.Remove } ?: LayoutButtons.fileTree.structure
//    }

//    val rightSidebar = prefs.serializable<LayoutStructure>(viewModelScope, "rightSidebar", LayoutStructure.Remove)
//    val bottomBar = MutableStateFlow<LayoutStructure>(LayoutStructure.Remove)//prefs.serializable<LayoutStructure>(viewModelScope, "bottomBar", LayoutStructure.Remove)

    //    val tabs = prefs.serializable<LayoutStructure.Tabbed>(viewModelScope, "tabs", LayoutStructure.Tabbed(emptyList()))
    val tabs = MutableStateFlow<List<LayoutDefinition>>(listOf(LayoutDefinition.Empty))
    val selectedTab = MutableStateFlow(0)
    val activeTab = viewModelScope.combinedStateFlow(selectedTab, tabs) { selected, tabs ->
        tabs.getOrNull(selected) ?: LayoutDefinition.Empty
    }

    val layoutButtonLocations = MutableStateFlow(LayoutButtonLocations())

    private val openFilesChannel = Channel<FileStructure.File>()
    val openFilesFlow = openFilesChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            openFilesFlow.collectLatest { (content) ->
                //FIXME
                println("Clicked $content")
                replaceTab(LayoutDefinition.of(content))
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

    private val _activeLayout = MutableStateFlow<LayoutDefinition?>(null)

    fun setActiveLayout(layout: LayoutDefinition) {
        _activeLayout.update { layout }
    }

    fun switchTab(index: Int) = selectedTab.update { index }

    fun closeTab(index: Int) = tabs.update {
        if (index in it.indices) it.toMutableList().apply { removeAt(index) } else it
    }

    fun replaceTab(layout: LayoutDefinition) {
        tabs.update { currentTabs ->
            val index = selectedTab.value
            if (index in currentTabs.indices) {
                currentTabs.toMutableList().apply { set(index, layout) }
            } else {
                currentTabs + layout
            }
        }
    }

    fun openTab(layout: LayoutDefinition): Int {
        tabs.update { it.plus(layout) }
        return tabs.value.lastIndex + 1
    }

//    fun setLeftSidebar(layout: LayoutStructure.Single) {
//        _leftSidebar.update { layout }
//    }
}
