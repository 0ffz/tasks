package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.ui.elements.task.EspressoLibreColorScheme
import me.dvyy.tasks.tasks.ui.elements.task.SerializableColorScheme

class PreferencesViewModel(
    prefs: LocalPreferencesRepository,
) : ViewModel() {

    val theme = prefs.string(viewModelScope, "theme", "")

    val deserializedTheme = theme.map {
        if (it.isEmpty()) EspressoLibreColorScheme
        else AppFormats.json.decodeFromString(SerializableColorScheme.serializer(), it)
    }.catch {
        it.printStackTrace()
        emit(EspressoLibreColorScheme)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EspressoLibreColorScheme)

    val density = prefs.float(viewModelScope, "density", 1f)
    val splitHeight = prefs.float(viewModelScope, "splitHeight", 0.5f)
}
