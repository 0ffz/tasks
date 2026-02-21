package me.dvyy.tasks.layout.ui.layouts

//import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import kotlinx.coroutines.flow.collectLatest
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.AppDrawerIconButton
import me.dvyy.tasks.app.ui.elements.AppTopBarActions
import me.dvyy.tasks.app.ui.elements.PlatformTopBarContainer
import me.dvyy.tasks.core.ui.MultiplatformDragAndDropData
import me.dvyy.tasks.core.ui.modifiers.onMiddleMouseClick
import me.dvyy.tasks.core.ui.platformDragAndDropSource
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
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
fun TintedHorizontalDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier.alpha(0.6f))
}

@Composable
fun TintedVerticalDivider(modifier: Modifier = Modifier) {
    VerticalDivider(modifier.alpha(0.6f))
}

@Composable
fun TabbedLayout(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) {
    val topRight by layoutViewModel.topRightLayout.collectAsState()
    val topRow by layoutViewModel.topRow.collectAsState()
    val topLeft by layoutViewModel.topLeftLayout.collectAsState()

    val selectable = Modifier.optional(structure.selectable) {
        pointerInput(structure) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    layoutViewModel.setActiveLayout(structure)
                }
            }
        }
    }

    Box(Modifier.fillMaxSize().then(selectable)) {
        Column {
            Surface(Modifier.fillMaxWidth(), tonalElevation = UI.elevation.lv1) {
                Row(modifier = Modifier.height(UI.tabHeight)) {
                    if (topLeft == structure && UI.isSmall) {
                        AppDrawerIconButton()
                    }
                    if (structure in topRow)
                        PlatformTopBarContainer {
                            if (topRight != structure)
                                Tabs(structure, onLayoutUpdate, layoutViewModel)
                            else FixedEndLayout(end = { AppTopBarActions() }) {
                                Row {
                                    Tabs(structure, onLayoutUpdate, layoutViewModel)
                                }
                            }
                        }
                    else Tabs(structure, onLayoutUpdate, layoutViewModel)
                }
            }

            TintedHorizontalDivider()

            Surface {
                structure.tabs.getOrNull(structure.selected)?.let {
                    Layout(it, onLayoutUpdate = { new -> onLayoutUpdate(new) })
                } ?: run {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "No tab is open",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { onLayoutUpdate(structure.withTab(LayoutStructure.Single.WeekView())) }) {
                            Text("Open week view")
                        }
                    }
                }
                DropTarget(structure, onLayoutUpdate)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Tabs(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) = BoxWithConstraints {
    val ui = LocalUIState.current
    val active by layoutViewModel.activeLayout.collectAsState()
    val isActive = structure == active
    val minTabWidth = 150.dp
    val maxTabWidth = 200.dp

    fun onTabbedUpdate(structure: LayoutStructure) {
        onLayoutUpdate(structure)
        layoutViewModel.setActiveLayout(structure)
    }
    if (isActive) LaunchedEffect(structure) {
        layoutViewModel.openFilesFlow.collectLatest { (content) ->
            onTabbedUpdate(structure.withTab(content))
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            if (structure.fullWidth) {
                Box(
                    Modifier.padding(ui.tabPadding),
                    contentAlignment = Alignment.CenterStart
                ) {
                    structure.tabs.firstOrNull()?.tabLabel(Location.TabList)
                }
            } else structure.tabs.forEachIndexed { index, tab ->
                Box(
                    Modifier.clickable {
                        onTabbedUpdate(structure.copy(selected = index))
                    }.onMiddleMouseClick {
                        closeTab(index)
                    }.widthIn(
                        max = (this@BoxWithConstraints.maxWidth / structure.tabs.size)
                            .coerceIn(minTabWidth, maxTabWidth)
                    ).platformDragAndDropSource {
                        closeTab(index)
                        MultiplatformDragAndDropData(tab, it)
                    }
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
                            tab.tabLabel(if (index == structure.selected) Location.Selected else Location.TabList)
                        }
                    }
                    if (index == structure.selected) Surface(
                        modifier = Modifier
                            .height(UI.size.xsm)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ) { }
                    HoverBox(
                        Modifier.fillMaxSize(),
                        onDropped = { new -> onTabbedUpdate(structure.withTab(new, atIndex = index)) }
                    )
                }
            }
        }
        HoverBox(
            Modifier.fillMaxSize().weight(1f),
            onDropped = { new -> onTabbedUpdate(structure.withTab(new)) }
        )
    }

    if (maxWidth < minTabWidth * structure.tabs.size) Box(
        Modifier.width(UI.size.lg).fillMaxHeight().background(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv1))
            )
        ).align(Alignment.CenterEnd)
    )
}
