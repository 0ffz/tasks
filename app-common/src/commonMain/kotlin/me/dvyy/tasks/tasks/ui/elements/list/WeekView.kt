package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.Cursors
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskListProperties
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.utils.Loadable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt


@Composable
fun WeekView(
    tasksViewModel: TasksViewModel = viewModel(),
    app: AppState = koinInject(),
    time: TimeViewModel = koinViewModel(),
    startAtToday: Boolean = false,
    takeDays: Int = 7,
) {
    val ui = LocalUIState.current
    val scrollState = rememberScrollState()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = app.snackbarHostState) }
    ) {
        val reorderInteractions = tasksViewModel.reorderInteractions()
        val ui = LocalUIState.current
        val columns = if (ui.isSingleColumn) 1 else takeDays
        val weekStart by (if (startAtToday) time.today else time.weekStart).collectAsState()
        val datesScrollable = if (ui.isSingleColumn)
            Modifier/*.nestedScroll(scrollBehavior.nestedScrollConnection)*/.verticalScroll(scrollState)
        else Modifier
        val today by time.today.collectAsState()
        NonlazyGrid(
            columns = columns,
            itemCount = takeDays,
            modifier = Modifier.fillMaxSize().then(datesScrollable),
        ) { dayIndex ->
            val day = weekStart.plus(DatePeriod(days = dayIndex))
            val isToday = day == today
            val listId = ListId.forDate(day)
            val properties by tasksViewModel.getListProperties(listId).collectAsState()
            val tasks by tasksViewModel.tasksFor(listId).collectAsState()
            var scrollToPosition by remember { mutableStateOf(0F) }
            TaskList(
                listId = listId,
                tasks = tasks,
                properties = properties,
                colored = isToday,
                viewModel = tasksViewModel,
                reorderInteractions = reorderInteractions,
                interactions = tasksViewModel.listInteractionsFor(listId),
                scrollable = !ui.isSingleColumn,
                modifier = Modifier.onGloballyPositioned { coords ->
                    scrollToPosition = coords.positionInRoot().y
                }
            )
            LaunchedEffect(Unit) {
                if (isToday && columns == 1) snapshotFlow { scrollToPosition }
                    .drop(1)
                    .take(1)
                    .collectLatest { scrollState.scrollTo(scrollToPosition.roundToInt()) }
            }
        }
    }
}

operator fun Orientation.not() = if (this == Orientation.Vertical) Orientation.Horizontal else Orientation.Vertical

inline fun Modifier.thenOptional(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) then(Modifier.modifier()) else this

@Composable
fun Divider(
    orientation: Orientation = Orientation.Vertical,
    toggleable: Boolean = false,
    onToggle: (expanded: Boolean) -> Unit = {},
    applyHoverCursor: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val ver = orientation == Orientation.Vertical
    var expanded by remember { mutableStateOf(true) }
    val padding = if (toggleable) 32.dp else 0.dp
    val clickable = if (toggleable) Modifier.clickable {
        expanded = !expanded
        onToggle(expanded)
    } else Modifier
    val surface = DividerDefaults.color
    val hover = MaterialTheme.colorScheme.primary
    var color by remember { mutableStateOf(surface) }
    val icon = if (ver) Cursors.horizontalResize else Cursors.verticalResize
    Box(
        modifier
            .thenOptional(applyHoverCursor) { pointerHoverIcon(icon) }
            .onHoverIfAvailable(onEnter = { color = hover }, onExit = { color = surface })
            .then(clickable),
    ) {
        if (toggleable) Box(Modifier.align(Alignment.TopEnd)) {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Icon(Icons.Rounded.ArrowDropDown, "Toggle", modifier = Modifier.rotate(rotation))
        }
        Box(
            Modifier.align(Alignment.Center)
        ) {
            val animatedColor by animateColorAsState(color)
            if (ver) HorizontalDivider(Modifier.padding(end = padding), color = animatedColor)
            else VerticalDivider(Modifier.padding(top = padding), color = animatedColor)
        }
    }
}

@Composable
fun DividerPill(orientation: Orientation = Orientation.Vertical) {
    val ui = LocalUIState.current
    val hor = orientation == Orientation.Vertical
    val icon = if (hor) Cursors.horizontalResize else Cursors.verticalResize
    fun Modifier.horWidth(amount: Dp) = if (hor) width(amount) else height(amount)
    fun Modifier.horHeight(amount: Dp) = if (hor) height(amount) else width(amount)

    Box(
        Modifier.horHeight(ui.dividerHeight).pointerHoverIcon(icon),
        contentAlignment = Alignment.Center
    ) {
        if (hor) HorizontalDivider() else VerticalDivider()
        Surface(Modifier.horHeight(8.dp).horWidth(220.dp)) {}
        Surface(
            shape = MaterialTheme.shapes.small,
            tonalElevation = 2.dp,
            modifier = Modifier.horHeight(8.dp).horWidth(200.dp)
        ) { }
    }
}

@Composable
fun Project(
    key: ListId,
    properties: Loadable<TaskListProperties>,
    tasksViewModel: TasksViewModel = viewModel(),
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tasks by tasksViewModel.tasksFor(key).collectAsState()
    val reorderInteractions = tasksViewModel.reorderInteractions()
    val ui = LocalUIState.current

    TaskList(
        listId = key,
        tasks = tasks,
        properties = properties,
        viewModel = tasksViewModel,
        reorderInteractions = reorderInteractions,
        interactions = tasksViewModel.listInteractionsFor(key),
        modifier = modifier,
        scrollable = scrollable
    )
}

@Composable
private fun <T> ProjectLayout(
    modifier: Modifier = Modifier,
    staggered: Boolean,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
) {
    val ui = LocalUIState.current
    when {
        staggered -> LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Adaptive(ui.taskListWidth)
        ) {
            items(items) { itemContent(it) }
        }

        else -> LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(ui.taskListWidth)
        ) {
            items(items) { itemContent(it) }
        }
    }
}

@Composable
fun AllProjectsView(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = viewModel(),
    staggered: Boolean,
) {
    val ui = LocalUIState.current
    val projects by tasksViewModel.projects.collectAsState()
    ProjectLayout(modifier, staggered, projects) { key ->
        val properties by tasksViewModel.getListProperties(key).collectAsState()
        Project(
            tasksViewModel = tasksViewModel,
            key = key,
            properties = properties,
            modifier = Modifier.width(ui.taskListWidth),
            scrollable = false
        )
//        Spacer(Modifier.height(32.dp))
    }
}
