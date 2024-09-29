package me.dvyy.tasks.layout.ui.layouts

//import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.fastMap
import kotlinx.coroutines.flow.collectLatest
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.AppTopBarActions
import me.dvyy.tasks.app.ui.elements.PlatformTopBarContainer
import me.dvyy.tasks.core.ui.modifiers.onMiddleMouseClick
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FixedEndLayout(
    modifier: Modifier = Modifier,
    end: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val endPlaceables = subcompose("end") {
            end()
        }.fastMap {
            it.measure(constraints)
        }
        val tabs = subcompose("tabs") {
            content()
        }.fastMap {
            it.measure(constraints.copy(maxWidth = layoutWidth - endPlaceables.sumOf { it.width }))
        }
//                    val topBarPlaceables =
//                        subcompose("topBar", {
//                            Tabs(structure, onLayoutUpdate, layoutViewModel)
//                        }).fastMap {
//                            it.measure(looseConstraints)
//                        }
        layout(layoutWidth, layoutHeight) {
            endPlaceables.forEach {
                it.placeRelative(layoutWidth - it.width, 0)
            }
            tabs.forEach {
                it.placeRelative(0, 0)
            }
        }
    }
}
@Composable
fun TabbedLayout(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) {
    val topRight by layoutViewModel.topRightLayout.collectAsState()
    val topRow by layoutViewModel.topRow.collectAsState()

    Box(Modifier.fillMaxSize().pointerInput(structure) {
        awaitPointerEventScope {
            while (true) {
                awaitFirstDown(pass = PointerEventPass.Initial)
                println("Pressed!")
                layoutViewModel.setActiveLayout(structure)
            }
        }
    }) {
        Column {
            if (structure in topRow)
                PlatformTopBarContainer {
                    if (topRight != structure)
                        Tabs(structure, onLayoutUpdate, layoutViewModel)
                    else FixedEndLayout(
                        modifier = Modifier.height(UI.tabHeight),
                        end = {
                            Surface(tonalElevation = UI.elevation.lv1) {
                                AppTopBarActions()
                            }
                        }
                    ) {
                        Row {
                            Tabs(structure, onLayoutUpdate, layoutViewModel)
                        }
                    }
//                Row(
//                    Modifier.height(UI.tabHeight),
//                    horizontalArrangement = Arrangement.End,
//                ) {
//                    Spacer(Modifier.weight(1f))
//                    AppTopBarActions()
//                }
                }
            else Tabs(structure, onLayoutUpdate, layoutViewModel)

            HorizontalDivider(Modifier.alpha(0.6f))

            structure.tabs.getOrNull(structure.selected)?.let {
                Layout(it, onLayoutUpdate = { new -> onLayoutUpdate(new) })
            }
        }
        DropTarget(structure, onLayoutUpdate)
    }
}


@Composable
private fun Tabs(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) = BoxWithConstraints {
    val ui = LocalUIState.current
    val active by layoutViewModel.activeLayout.collectAsState()
    val isActive = structure == active

    fun onTabbedUpdate(structure: LayoutStructure) {
        onLayoutUpdate(structure)
        layoutViewModel.setActiveLayout(structure)
    }
    if (isActive) LaunchedEffect(structure) {
        layoutViewModel.openFilesFlow.collectLatest { (content) ->
            onTabbedUpdate(structure.withTab(content))
        }
    }

    Surface(Modifier.fillMaxWidth(), tonalElevation = UI.elevation.lv1) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(UI.tabHeight)
        ) {
            fun closeTab(index: Int) {
                if (structure.tabs.size == 1) {
                    onTabbedUpdate(LayoutStructure.Empty)
                } else onTabbedUpdate(
                    structure.copy(
                        tabs = structure.tabs.toMutableList().apply { removeAt(index) },
                        selected = (structure.selected - 1).coerceAtLeast(0)
                    )
                )
            }
            structure.name?.let {
                Box(Modifier.padding(UI.padding.sm)) {
                    Text(it, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(UI.padding.sm))
            if (structure.fullWidth) {
                Box(
                    Modifier.padding(ui.tabPadding),
                    contentAlignment = Alignment.CenterStart
                ) {
                    structure.tabs.firstOrNull()?.tabLabel(false)
                }
            } else structure.tabs.forEachIndexed { index, tab ->
                Box(
                    Modifier.clickable {
                        onTabbedUpdate(structure.copy(selected = index))
                    }.onMiddleMouseClick {
                        closeTab(index)
                    }//.width(IntrinsicSize.Max)
                        .widthIn(max = min(200.dp, this@BoxWithConstraints.maxWidth / structure.tabs.size))
                ) {
                    FixedEndLayout(
                        Modifier.padding(ui.tabPadding).height(ui.tabHeight),
                        end = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(Modifier.width(UI.padding.sm))
                                IconButton(onClick = {
                                    closeTab(index)
                                }, modifier = Modifier.size(UI.size.lg)) {
                                    Icon(
                                        AppIcons.Close,
                                        "Close tab",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            tab.tabLabel(index == structure.selected)
                        }
                    }
                    if (index == structure.selected && !structure.fullWidth) Surface(
                        modifier = Modifier
                            .height(UI.size.xsm)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    ) { }
                }
            }
        }
    }
}
