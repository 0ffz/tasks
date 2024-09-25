package me.dvyy.tasks.tree.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel
import me.dvyy.tasks.tasks.ui.elements.list.thenOptional

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

@Composable
fun FileEntry(
    file: FileStructure,
    layout: LayoutViewModel = koinViewModel(),
) = Column {
    var open by remember { mutableStateOf(false) }
    val clickable = Modifier.thenOptional(file !is FileStructure.Element) {
        clickable {
            when (file) {
                is FileStructure.Folder -> open = !open
                is FileStructure.File -> {
                    layout.activeContentView.update { LayoutStructure.Single { file.opensLayout() } }
                    file.onClick()
                }
                else -> {}
            }
        }
    }
    Box(
        modifier = clickable.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            when (file) {
                is FileStructure.File -> {
                    Icon(file.icon ?: Icons.Rounded.FilePresent, "File")
                    Spacer(Modifier.width(4.dp))
                    Text(file.name)
                }

                is FileStructure.Folder -> {
                    Icon(Icons.Rounded.Folder, "Folder")
                    Spacer(Modifier.width(4.dp))
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
            Box(Modifier.padding(start = 16.dp)) {
                FileList(file.children)
            }
        }
    }
}

sealed interface FileStructure {
    val name: String

    data class Element(
        override val name: String = "Unnamed",
        val content: @Composable () -> Unit,
    ) : FileStructure

    data class File(
        override val name: String,
        val icon: ImageVector? = null,
        val opensLayout: @Composable () -> Unit,
        val onClick: () -> Unit = {},
    ) : FileStructure

    data class Folder(
        override val name: String,
        val children: List<FileStructure>,
    ) : FileStructure
}
