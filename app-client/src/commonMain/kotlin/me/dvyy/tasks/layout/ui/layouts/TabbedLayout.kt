package me.dvyy.tasks.layout.ui.layouts

//import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Plus
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.PlatformTopBarContainer
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.core.ui.modifiers.onMiddleMouseClick
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.WeekView
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import org.kodein.di.compose.viewmodel.rememberViewModel
import kotlin.uuid.Uuid

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
) {
    val layoutViewModel: LayoutViewModel by rememberViewModel()
//    val topRight by layoutViewModel.topRightLayout.collectAsState()
//    val topRow by layoutViewModel.topRow.collectAsState()
//    val topLeft by layoutViewModel.topLeftLayout.collectAsState()

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
//                    if (topLeft == structure && UI.isSmall) {
//                        AppDrawerIconButton()
//                    }
//                    if (structure in topRow)
//                        if (topRight != structure)
//                            Tabs(structure, onLayoutUpdate, layoutViewModel)
//                        else FixedEndLayout(end = { AppTopBarActions() }) {
//                            Row {
//                                Tabs(structure, onLayoutUpdate, layoutViewModel)
//                            }
//                        }
                    Tabs(structure, onLayoutUpdate)
                }
            }

            TintedHorizontalDivider()

            Surface {
                structure.tabs.getOrNull(structure.selected)?.let {
                    Layout(it, onLayoutUpdate = { new ->
                        onLayoutUpdate(structure.withTab(new, atIndex = structure.selected, replace = true))
                    })
                } ?: EmptyLayout(onLayoutUpdate = { layoutViewModel.openTab(WeekView()) })
//                if (structure.tabs.getOrNull(structure.selected)?.hasDropTargets != false)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun Tabs(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
) = BoxWithConstraints {
    val layoutViewModel: LayoutViewModel by rememberViewModel()
    val ui = LocalUIState.current
    val minTabWidth = 150.dp
    val maxTabWidth = 200.dp

    fun onTabbedUpdate(structure: LayoutStructure) {
        onLayoutUpdate(structure)
        layoutViewModel.setActiveLayout(structure)
    }
//    if (isActive) LaunchedEffect(structure) {
//        layoutViewModel.openFilesFlow.collectLatest { (content) ->
//            onTabbedUpdate(structure.withTab(content))
//        }
//    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .height(UI.tabHeight)
    ) {
        fun closeTab(index: Int) {
            if (structure.tabs.size == 1) {
                onTabbedUpdate(LayoutStructure.Remove)
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
                    (structure.tabs.firstOrNull() as? LayoutStructure.Single)?.tabLabel(Location.TabList)
                }
            } else structure.tabs.forEachIndexed { index, tab ->
                Box(
                    Modifier.widthIn(
                        max = (this@BoxWithConstraints.maxWidth / structure.tabs.size)
                            .coerceIn(minTabWidth, maxTabWidth)
                    )
                ) {
                    LayoutTab(
                        tab,
                        selected = index == structure.selected,
                        onDropLayout = { new -> onTabbedUpdate(structure.withTab(new, atIndex = index)) },
                        onSelect = {
                            onTabbedUpdate(structure.copy(selected = index))
                        },
                        onClose = { closeTab(index) }
                    )
                }
            }
        }
        if (structure.selectable) BoxButton(onClick = {
            layoutViewModel.openTab(LayoutStructure.Empty)
        }) {
            Icon(TablerIcons.Outlined.Plus, "Add tab", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        PlatformTopBarContainer(Modifier.fillMaxSize().weight(1f), {
            HoverBox(
                Modifier.fillMaxSize(),
                onDropped = { new -> onTabbedUpdate(structure.withTab(new)) }
            )
        })
    }

    if (maxWidth < minTabWidth * structure.tabs.size) Box(
        Modifier.width(UI.size.lg).fillMaxHeight().background(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceColorAtElevation(UI.elevation.lv1))
            )
        ).align(Alignment.CenterEnd)
    )
}

@Composable
fun LayoutTab(
    tab: LayoutStructure,
    modifier: Modifier = Modifier,
    selected: Boolean,
    showCloseButton: Boolean = true,
    onDropLayout: (LayoutStructure) -> Unit = {},
    onSelect: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    val ui = UI
    Box(
        modifier.clickableWithoutRipple { onSelect() }
            .onMiddleMouseClick { onClose() },
        /*.dropTarget(
                    LocalDragAndDropState.current,
                    shouldStartDragAndDrop = { it.data is Dragged.Layout },
                ) {
                    Logger.i { "Dropped ${it.data} on $index" }
                    onTabbedUpdate(structure.copy(selected = index))
                }*/
    ) {
        DraggableItem(
            key = remember { Uuid.random() },
//                        requireFirstDownUnconsumed = true,
            data = Dragged.Layout(tab),
            onDragStart = { onClose() },
            state = LocalDragAndDropState.current
        ) {
            FixedEndLayout(
                Modifier.height(ui.tabHeight),
                end = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(UI.padding.sm))
                        if (showCloseButton) BoxButton(onClick = {
                            onClose()
                        }) {
                            Icon(
                                TablerIcons.Outlined.X,
                                "Close tab",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(ui.tabPadding)) {
                    (tab as? LayoutStructure.Single)?.tabLabel(if (selected) Location.Selected else Location.TabList)
                        ?: run {
                            Text(
                                "Custom Layout",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            //TODO implement layout saving
//                            val tasksViewModel = koinViewModel<TasksViewModel>()
//                            BoxButton(onClick = { tasksViewModel.saveLayout(tab) }) {
//                                Icon(AppIcons.Save, "Save layout")
//                            }
                        }
                }
            }
            if (selected) Surface(
                modifier = Modifier
                    .height(UI.size.xsm)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = if (true/*isActive*/) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            ) { }
            HoverBox(
                Modifier.fillMaxSize(),
                onDropped = { new -> onDropLayout(new) }
            )
        }
    }
}