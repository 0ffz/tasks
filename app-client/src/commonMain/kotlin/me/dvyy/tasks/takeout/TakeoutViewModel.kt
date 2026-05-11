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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.buffered

data class ImportProgressUiState(val current: Int, val total: Int)

class TakeoutViewModel(
    val repository: TakeoutRepository,
) : ViewModel() {
    private val _importProgress = MutableStateFlow<ImportProgressUiState?>(null)
    val importProgress: StateFlow<ImportProgressUiState?> = _importProgress.asStateFlow()

    fun startImport() = viewModelScope.launch {
        FileKit.openFilePicker(type = FileKitType.File("json"))
            ?.source()
            ?.buffered()
            ?.use { source ->
                _importProgress.value = ImportProgressUiState(0, 0)
                repository.import(source) { current, total ->
                    _importProgress.value = ImportProgressUiState(current, total)
                }
            }
        _importProgress.value = null
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
