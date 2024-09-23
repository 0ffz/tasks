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
import androidx.compose.ui.unit.dp

@Composable
fun FileList(files: List<FileStructure>) {
    Column {
        files.forEach {
            FileEntry(it)
        }
    }
}

@Composable
fun FileEntry(file: FileStructure) = Column {
    var open by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { open = !open }
            .padding(4.dp)
    ) {
        when (file) {
            is FileStructure.File -> {
                Icon(Icons.Rounded.FilePresent, "File")
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

    data class File(
        override val name: String,
    ) : FileStructure

    data class Folder(
        override val name: String,
        val children: List<FileStructure>,
    ) : FileStructure
}
