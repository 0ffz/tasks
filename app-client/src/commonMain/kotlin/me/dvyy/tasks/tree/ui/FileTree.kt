package me.dvyy.tasks.tree.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun FileList(
    files: List<FileStructure>,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        files.forEach {
            FileEntry(it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileEntry(
    file: FileStructure,
    layout: LayoutViewModel = koinViewModel(),
) = Column {
    var open by remember { mutableStateOf(false) }
//    val clickable = Modifier.optional(file !is FileStructure.Element) {
//        clickable {
//            when (file) {
//                is FileStructure.Folder -> open = !open
//                is FileStructure.File -> {
//                    layout.openInActiveView(file)
//                    file.onClick()
//                }
//
//                else -> {}
//            }
//        }
//    }

    if (file is FileStructure.File) DraggableItem(
        key = remember { Uuid.random() },
//                        requireFirstDownUnconsumed = true,
        data = Dragged.Layout(file.opensLayout),
        onDragStart = { file.onStartDrag() },
        state = LocalDragAndDropState.current
    ) {
        val onDropTask = file.onDropTask
        Surface(
            modifier = Modifier.fillMaxWidth()
                .clickable {
                    when (file) {
                        is FileStructure.Folder -> open = !open
                        is FileStructure.File -> {
                            layout.openInActiveView(file)
                            file.onClick()
                        }

                        else -> {}
                    }
                }
                .optional(onDropTask != null) {
                    dropTarget(
                        remember { Uuid.random() },
                        LocalDragAndDropState.current,
                        shouldStartDragAndDrop = { it.data is Dragged.Task },
                    ) {
                        val task = (it.data as? Dragged.Task)?.uuid ?: return@dropTarget
                        file.onDropTask?.let { it1 -> it1(task.asTask()) }
                    }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(UI.padding.sm)
            ) {
                when (file) {
                    is FileStructure.File -> {
                        file.opensLayout.tabLabel(Location.Sidebar)
                    }

                    is FileStructure.Folder -> {
                        Icon(Icons.Rounded.Folder, "Folder")
                        Spacer(Modifier.width(UI.padding.sm))
                        Text(file.name)
                        Spacer(Modifier.weight(1f))
                        val rotation by animateFloatAsState(if (open) 180f else 0f)
                        Icon(Icons.Rounded.ArrowDropDown, "Toggle", modifier = Modifier.rotate(rotation))
                    }

                    is FileStructure.Element -> file.content()
                }
            }
        }
    }
    if (file is FileStructure.Folder) {
        AnimatedVisibility(visible = open) {
            Box(Modifier.padding(start = UI.padding.xl)) {
                FileList(file.children)
            }
        }
    }
}

sealed interface FileStructure {
    data class Element(
        val content: @Composable () -> Unit,
    ) : FileStructure

    data class File(
        val opensLayout: LayoutStructure.Single,
        val onClick: () -> Unit = {},
        val onStartDrag: () -> Unit = {},
        val onDropTask: ((TaskId) -> Unit)? = null,
    ) : FileStructure

    data class Folder(
        val name: String,
        val children: List<FileStructure>,
    ) : FileStructure
}
