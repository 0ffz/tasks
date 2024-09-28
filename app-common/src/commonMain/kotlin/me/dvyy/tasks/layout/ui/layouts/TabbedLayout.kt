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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.layout.ui.Layout
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TabbedLayout(
    structure: LayoutStructure.Tabbed,
    onLayoutUpdate: (LayoutStructure) -> Unit = {},
    layoutViewModel: LayoutViewModel = koinViewModel(),
) {
    val active by layoutViewModel.activeLayout.collectAsState()
    val isActive = structure == active

    fun onTabbedUpdate(structure: LayoutStructure) {
        onLayoutUpdate(structure)
        layoutViewModel.setActiveLayout(structure)
    }

    Box(Modifier.fillMaxSize().pointerInput(structure) {
        awaitPointerEventScope {
            while (true) {
                awaitFirstDown(pass = PointerEventPass.Initial)
                println("Pressed!")
                layoutViewModel.setActiveLayout(structure)
            }
        }
    }) {
        if (isActive) LaunchedEffect(structure) {
            layoutViewModel.openFilesFlow.collectLatest { (content) ->
                onTabbedUpdate(structure.withTab(content))
            }
        }

        Column {
            Surface(Modifier.fillMaxWidth(), tonalElevation = 1.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
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
                        Box(Modifier.padding(6.dp)) {
                            Text(it, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    structure.tabs.forEachIndexed { index, tab ->
                        Box(
                            Modifier.clickable {
                                onTabbedUpdate(structure.copy(selected = index))
                            }/*.onClick(matcher = PointerMatcher.mouse(PointerButton.Tertiary)) {
                            closeTab(index)
                        }*/.width(IntrinsicSize.Max)
                        ) {
                            Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                tab.tabLabel()
                                Spacer(Modifier.width(4.dp))
                                IconButton(onClick = {
                                    closeTab(index)
                                }, modifier = Modifier.size(18.dp)) {
                                    Icon(
                                        AppIcons.Close,
                                        "Close tab",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (index == structure.selected) Surface(
                                modifier = Modifier
                                    .height(2.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            ) { }
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.alpha(0.6f))
            structure.tabs.getOrNull(structure.selected)?.let {
                Layout(
                    it,
                    onLayoutUpdate = { new -> onLayoutUpdate(new) }
                )
            }
        }
        DropTarget(structure, onLayoutUpdate)
    }
}
