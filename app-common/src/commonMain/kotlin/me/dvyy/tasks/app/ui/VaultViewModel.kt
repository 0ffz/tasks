package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.model.Note
import me.dvyy.tasks.database.model.NoteFrontMatter
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.tree.ui.FileStructure
import me.dvyy.tasks.utils.WhileUiSubscribed

class VaultViewModel(
    val vault: Vault,
    val dialogViewModel: DialogViewModel,
) : ViewModel() {
    val fileTree = vault.fileTree()
        .map { toFileStructure(it) }
        .stateIn(viewModelScope, WhileUiSubscribed, emptyList())

    fun toFileStructure(list: List<VaultPath>, depth: Int = 0): List<FileStructure> {
        val mapped = list.groupBy {
            it.pathString.splitToSequence("/").drop(depth).firstOrNull()
                ?.takeIf { folder -> !it.pathString.endsWith(folder) }
        }
        val files = mapped[null]?.map { FileStructure.File(LayoutStructure.Single.Project(it)) } ?: emptyList()
        val folders = mapped.mapNotNull { (folder, paths) ->
            if (folder == null) null
            else FileStructure.Folder(folder, toFileStructure(paths, depth + 1))
        }
        return folders + files
    }

    fun observeDocument(path: VaultPath): StateFlow<Note?> =
        vault.observeDocument(path).stateIn(viewModelScope, WhileUiSubscribed, null)

    fun deleteDocument(path: VaultPath) {
        viewModelScope.launch {
            vault.deleteDocument(path)
        }
    }

    fun updateContent(path: VaultPath, content: String) {
        viewModelScope.launch {
            vault.update(path) {
                this.fileContent = content
            }
        }
    }

    inline fun updateFrontMatter(
        path: VaultPath,
        clearOld: Boolean = false,
        crossinline update: NoteFrontMatter.() -> Unit,
    ) = viewModelScope.launch {
        vault.update(path, clearOldFrontMatter = clearOld) { update(this.frontMatter) }
    }

    fun convertToNote(path: VaultPath) {
        updateFrontMatter(path) { managed = false }
    }

    init {
        viewModelScope.launch {
            vault.index()
        }
    }
}
