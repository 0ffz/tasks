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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.MultiplatformDragAndDropData
import me.dvyy.tasks.core.ui.platformDragAndDropSource
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.tasks.ui.elements.list.optional
import org.koin.compose.viewmodel.koinViewModel

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
    val clickable = Modifier.optional(file !is FileStructure.Element) {
        clickable {
            when (file) {
                is FileStructure.Folder -> open = !open
                is FileStructure.File -> {
                    layout.openInActiveView(file)
                    file.onClick()
                }

                else -> {}
            }
        }
    }
    Box(
        modifier = clickable.fillMaxWidth()
            .platformDragAndDropSource {
                if (file is FileStructure.File) {
                    file.onStartDrag()
                    MultiplatformDragAndDropData(file.opensLayout, it)
                } else null
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
    ) : FileStructure

    data class Folder(
        val name: String,
        val children: List<FileStructure>,
    ) : FileStructure
}
