package me.dvyy.tasks.tree.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import dev.seyfarth.tablericons.outlined.Trash
import kotlinx.collections.immutable.ImmutableList
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.rememberGlobalViewModel
import me.dvyy.tasks.core.ui.PlatformSpecifics
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.core.ui.modifiers.onHoverIfAvailable
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.LayoutTab
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonColumn
import me.dvyy.tasks.tasks.ui.elements.helpers.optional
import me.dvyy.tasks.utils.Dragged
import me.dvyy.tasks.utils.LocalDragAndDropState
import kotlin.uuid.Uuid

@Composable
fun FileList(
    files: ImmutableList<FileStructure>,
    onPromptDeleteProject: (ListId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        files.forEach {
            FileEntry(it, onPromptDeleteProject)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileEntry(
    file: FileStructure,
    onPromptDeleteProject: (ListId) -> Unit,
    modifier: Modifier = Modifier,
) = ButtonColumn(modifier) {
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
            var visible by remember { mutableStateOf(!PlatformSpecifics.hoverAvailable) }
            LayoutTab(false, LayoutDefinition.of(file.opensLayout), onClick = {
                when (file) {
                    is FileStructure.Folder -> open = !open
                    is FileStructure.File -> {
                        layout.openInActiveView(file)
                        file.onClick()
                    }

                    else -> {}
                }
            }, Modifier.onHoverIfAvailable(onEnter = { visible = true }, onExit = { visible = false }), trailingOptions = {
                val project = (file.opensLayout as? ScreenDest.Project)?.id ?: return@LayoutTab
                if (visible) BoxButton(AppIcons.Trash, onClick = { onPromptDeleteProject(project) }, tooltip = "Remove project", tint = MaterialTheme.colorScheme.onSurfaceVariant.fade(0.75f))
            })
        }
    }
    if (file is FileStructure.Folder) {
        AnimatedVisibility(visible = open) {
            Box(Modifier.padding(start = UI.padding.xl)) {
                FileList(file.children, onPromptDeleteProject)
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
        val children: ImmutableList<FileStructure>,
    ) : FileStructure
}
