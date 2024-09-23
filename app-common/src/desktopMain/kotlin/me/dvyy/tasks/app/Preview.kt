package me.dvyy.tasks.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure

@Preview
@Composable
fun Preview() {
    AppTheme {
        Scaffold {
            FileList(
                listOf(
                    FileStructure.File("file1"),
                    FileStructure.Folder(
                        "folder1", listOf(
                            FileStructure.File("file2"),
                            FileStructure.File("file3"),
                        )
                    ),
                    FileStructure.File("file4"),
                )
            )
        }
    }
}
