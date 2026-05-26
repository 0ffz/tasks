package me.dvyy.tasks.tree.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.rememberGlobalViewModel
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.ScreenTab
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
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
    file: FileStructure, modifier: Modifier = Modifier,
) = Column(modifier) {
    val layout: LayoutViewModel by rememberGlobalViewModel()
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
        data = Dragged.Layout(file.opensLayout, source = null),
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
                        (state.data as? Dragged.Task)?.uuid
                        //FIXME reimplement
//                        val list = ((state.data as? Dragged.Layout)?.layout as? ScreenDest.Project)?.key
//                        when {
//                            task != null -> file.onDropTask?.let { it(task.asTask()) }
//                            list != null -> file.onDropList?.let { it(list) }
//                        }
                    }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(UI.tasks.height).padding(start = UI.padding.md),
            ) {
                ScreenTab(file.opensLayout)
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

@Stable
sealed interface FileStructure {
    data class Element(
        val content: @Composable () -> Unit,
    ) : FileStructure

    data class File(
        val opensLayout: ScreenDest,
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
