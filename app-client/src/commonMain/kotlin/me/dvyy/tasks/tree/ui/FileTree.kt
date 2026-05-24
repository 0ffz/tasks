package me.dvyy.tasks.tree.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asTask
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import org.kodein.di.compose.viewmodel.rememberViewModel
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
) = Column {
    val layout: LayoutViewModel by rememberViewModel()
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
        val onDropList = file.onDropList
        Box(
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
                .optional(onDropTask != null || onDropList != null) {
                    dropTarget(
                        LocalDragAndDropState.current,
                        shouldStartDragAndDrop = { it.data is Dragged.Task || it.data is Dragged.Layout },
                    ) { state ->
                        val task = (state.data as? Dragged.Task)?.uuid
                        val list = ((state.data as? Dragged.Layout)?.layout as? LayoutStructure.Single.Project)?.key
                        when {
                            task != null -> file.onDropTask?.let { it(task.asTask()) }
                            list != null -> file.onDropList?.let { it(list) }
                        }
                    }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(UI.tasks.height).padding(start = UI.padding.md),
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
        val onDropList: ((ListId) -> Unit)? = null,
    ) : FileStructure

    data class Folder(
        val name: String,
        val children: List<FileStructure>,
    ) : FileStructure
}
