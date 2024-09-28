package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import me.dvyy.tasks.app.data.LocalPreferencesRepository
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
    val prefs: LocalPreferencesRepository,
) : ViewModel() {
//    private fun decodeStructure(
//        key: String,
//        default: LayoutStructure = LayoutStructure.Empty,
//    ): LayoutStructure {
//        val json = settings.getStringOrNull(key) ?: return default
//        return runCatching { AppFormats.json.decodeFromString(LayoutStructure.serializer(), json) }
//            .getOrDefault(default)
//    }

    val leftSidebar = prefs.serializable<LayoutStructure>(viewModelScope, "leftSidebar", LayoutButtons.fileTree.structure)
    val rightSidebar = prefs.serializable<LayoutStructure>(viewModelScope, "rightSidebar", LayoutStructure.Empty)
    val bottomBar = prefs.serializable<LayoutStructure>(viewModelScope, "bottomBar", LayoutStructure.Empty)
    private val _mainView = prefs.serializable<LayoutStructure>(viewModelScope, "mainView", LayoutStructure.Empty)

    val mainView = _mainView
        .map(viewModelScope) { it.takeIf { it != LayoutStructure.Empty } ?: LayoutStructure.Tabbed(listOf(), 0) }

    val layoutButtonLocations = MutableStateFlow(LayoutButtonLocations())

    private val _activeLayout = MutableStateFlow<LayoutStructure?>(null)

    val activeLayout: StateFlow<LayoutStructure> = combine(_activeLayout, mainView) { active, main ->
        active.takeIf { it != LayoutStructure.Empty } ?: main
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mainView.value)

    private val openFilesChannel = Channel<FileStructure.File>()
    val openFilesFlow = openFilesChannel.receiveAsFlow()

    fun serialize(layout: LayoutStructure) {

    }

    init {
        layoutButtonLocations.update {
            LayoutButtonLocations(
                left = listOf(LayoutButtons.fileTree),
                bottom = listOf(LayoutButtons.projects),
            )
        }
    }

    fun openInActiveView(file: FileStructure.File) {
        openFilesChannel.trySend(file)
    }

    fun setActiveLayout(layout: LayoutStructure) {
        println("Old layout was ${_activeLayout.value}")
        println("Setting layout to $layout")
        _activeLayout.update { layout }
    }

    fun setMainView(layout: LayoutStructure) {
        _mainView.update { layout }
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
                first = left,
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
}
