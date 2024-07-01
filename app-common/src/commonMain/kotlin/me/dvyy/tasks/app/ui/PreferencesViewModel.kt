package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.model.serializers.AppFormats
import me.dvyy.tasks.tasks.ui.elements.task.ColorScheme
import me.dvyy.tasks.tasks.ui.elements.task.EspressoLibreColorScheme
import me.dvyy.tasks.tasks.ui.elements.task.SerializableColorScheme

class PreferencesViewModel(
    private val settings: Settings,
) : ViewModel() {
    private val debounceMillis = 500L

    val theme = stringSetting("theme", "")
    val deserializedTheme = theme.map {
        if (it.isEmpty()) EspressoLibreColorScheme
        else AppFormats.json.decodeFromString(SerializableColorScheme.serializer(), it)
    }.catch {
        it.printStackTrace()
        emit(EspressoLibreColorScheme)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), EspressoLibreColorScheme)
    val density = floatSetting("density", 1f)
    val splitHeight = floatSetting("splitHeight", 0.5f)

    private inline fun <T> setting(
        key: String,
        defaultValue: T,
        crossinline read: (Settings, String, T) -> T,
        crossinline write: (Settings, String, T) -> Unit,
    ): MutableStateFlow<T> {
        val cachedSetting = MutableStateFlow(read(settings, key, defaultValue))

        viewModelScope.launch(Dispatchers.Default) {
            cachedSetting.debounce(debounceMillis).collect {
                write(settings, key, it)
            }
        }

        return cachedSetting
    }

    private fun floatSetting(key: String, defaultValue: Float): MutableStateFlow<Float> {
        return setting(key, defaultValue, Settings::getFloat, Settings::set)
    }

    private fun stringSetting(key: String, defaultValue: String): MutableStateFlow<String> {
        return setting(key, defaultValue, Settings::getString, Settings::set)
    }
}
