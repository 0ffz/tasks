package me.dvyy.tasks.takeout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.sink
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.launch
import kotlinx.io.buffered

class TakeoutViewModel(
    val repository: TakeoutRepository,
) : ViewModel() {
    fun startImport() = viewModelScope.launch {
        FileKit.openFilePicker(type = FileKitType.File("json"))
            ?.source()
            ?.buffered()
            ?.use { source ->
                repository.import(source)
            }
    }

    fun startExport() = viewModelScope.launch {
        FileKit.openFileSaver("tasks_export", extension = "json")
            ?.sink()
            ?.buffered()
            ?.use { sink ->
                repository.export(sink)
            }
    }

    fun migrateOldDatabase() = viewModelScope.launch {
        val path = FileKit.openFilePicker(type = FileKitType.File("db"))?.absolutePath() ?: return@launch
        FileKit.openFileSaver("tasks_export", extension = "json")?.sink()?.buffered()?.use {
            repository.migrateOldDb(path, it)
        }
    }
}
