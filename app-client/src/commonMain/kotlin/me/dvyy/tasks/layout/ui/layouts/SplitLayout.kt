package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import org.kodein.di.compose.viewmodel.rememberViewModel
import kotlin.time.Duration.Companion.seconds

/**
 * A split layout, vertically or horizontally, with a draggable divider in the middle
 */
@OptIn(FlowPreview::class)
@Composable
fun SplitLayout(
    structure: LayoutStructure.Split,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
) {
    var splitAmount by remember { mutableStateOf(structure.split) }

    LaunchedEffect(structure) {
        snapshotFlow { splitAmount }.debounce(2.seconds).collectLatest {
            onLayoutUpdate(structure.copy(split = it))
        }
    }

    Split(
        structure.split,
        onSplitAmountChange = { splitAmount = it },
        orientation = structure.orientation,
        firstEnabled = structure.firstEnabled,
        secondEnabled = structure.secondEnabled,
        first = { Layout(structure.first) },
        second = { Layout(structure.second) }
    )
}
