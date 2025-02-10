package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.dvyy.tasks.app.data.LocalPreferencesRepository
import me.dvyy.tasks.app.ui.theme.DarkModePref
import me.dvyy.tasks.app.ui.theme.DefaultTheme
import me.dvyy.tasks.app.ui.theme.TaskAppTheme

class PreferencesViewModel(
    prefs: LocalPreferencesRepository,
) : ViewModel() {
    val darkMode = prefs.serializable<DarkModePref>(viewModelScope, "darkMode", DarkModePref.AUTO)

    val appTheme = prefs.serializable<TaskAppTheme>(
        viewModelScope,
        "appTheme",
        DefaultTheme()
    )

    val theme = prefs.string(viewModelScope, "theme", "")

//    val deserializedTheme = theme.map {
//        if (it.isEmpty()) EspressoLibreColorScheme
//        else AppFormats.json.decodeFromString(SerializableColorScheme.serializer(), it)
//    }.catch {
//        it.printStackTrace()
//        emit(EspressoLibreColorScheme)
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, EspressoLibreColorScheme)

    val density = prefs.float(viewModelScope, "density", 1f)
    val splitHeight = prefs.float(viewModelScope, "splitHeight", 0.5f)
}
