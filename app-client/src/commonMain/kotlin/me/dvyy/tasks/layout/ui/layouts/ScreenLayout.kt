package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.GripHorizontal
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.layout.ui.components.FloatingSurface
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.layout.ui.screens.builder.ScreenScope
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import kotlin.uuid.Uuid

@Composable
fun ScreenLayout(
    dest: ScreenDest,
    rootLayout: LayoutDefinition,
    layoutIndex: Int,
    onLayoutChange: (LayoutDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screen = remember(dest) { dest.toScreen() }
    var hovered by remember { mutableStateOf(false) }
    Box(modifier = modifier.onHoverIfAvailable(onEnter = { hovered = true }, onExit = { hovered = false })) {
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            floatingActionButton = {
//            FloatingSurface {
//                ButtonRow(Modifier.height(UI.tabHeight).width(IntrinsicSize.Max)) {
//                    LayoutTab(
//                        structure,
////                        Modifier.weight(1f),
//                        showCloseButton = false,
//                        selected = false,
//                        onClose = { onLayoutUpdate(LayoutStructure.Remove) },
//                        onDropLayout = onLayoutUpdate,
//                    )
//                }
//            }
//            Spacer(Modifier.weight(1f))
                FloatingSurface {
                    ButtonRow(Modifier.height(UI.tabHeight)) {
                        screen.trailingOptions()
//                    BoxButton(onClick = { onLayoutUpdate(LayoutStructure.Empty) }) {
//                        Icon(TablerIcons.Outlined.X, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
//                    }
//                    BoxButton(onClick = {}, modifier = Modifier.width(24.dp)) {
//                        Icon(TablerIcons.Outlined.GripVertical, "Drag", Modifier)
//                    }
                    }
                }
            }
        ) {
            Box(Modifier.padding(it)) {
                val scope = ScreenScope(
                    layout = rootLayout,
                    layoutIndex = layoutIndex,
                    onLayoutChange = onLayoutChange,
                )
                screen.content(scope)
//            screen.cachedContent()
            }
        }
        val cornerSize = 6.dp
        if (rootLayout.operations.size > 1 && hovered) Surface(
            Modifier.align(Alignment.TopCenter).alpha(0.8f),
            shape = RoundedCornerShape(bottomStart = cornerSize, bottomEnd = cornerSize),
            tonalElevation = 0.dp
        ) {
            Row(Modifier.padding(vertical = 3.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//            Icon(TablerIcons.Outlined.X, "Drag", Modifier.size(16.dp))
                DraggableItem(
                    key = remember { Uuid.random() },
                    data = Dragged.Layout(dest, source = rootLayout.operations[layoutIndex].id),
                    state = LocalDragAndDropState.current
                ) {
//                    IconButton(onClick = { onLayoutChange(rootLayout.minus(layoutIndex)) }, Modifier.size(16.dp)) {
                    Icon(TablerIcons.Outlined.GripHorizontal, "Drag", Modifier.size(16.dp))
//                    }
                }
                IconButton(onClick = { onLayoutChange(rootLayout.minus(layoutIndex)) }, Modifier.size(16.dp)) {
                    Icon(TablerIcons.Outlined.X, "Close")
                }
//            Icon(TablerIcons.Outlined.ArrowsDiagonal, "Drag", Modifier.size(16.dp))
            }
        }
//    if (screen.hasDropTargets) DropTarget(structure, onLayoutUpdate)
    }
}