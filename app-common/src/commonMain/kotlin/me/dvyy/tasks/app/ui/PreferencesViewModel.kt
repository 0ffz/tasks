package me.dvyy.tasks.app.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.debounce

class PreferencesViewModel(
    private val settings: Settings,
) : ViewModel() {
    private val debounceMillis = 500L

    @Composable
    private inline fun <T> setting(
        key: String,
        defaultValue: T,
        crossinline read: (Settings, String, T) -> T,
        crossinline write: (Settings, String, T) -> Unit,
    ): MutableState<T> {
        val cachedSetting = remember { mutableStateOf(read(settings, key, defaultValue)) }
        LaunchedEffect(Unit) {
            snapshotFlow { cachedSetting.value }.debounce(debounceMillis).collect {
                write(settings, key, it)
            }
        }
        return cachedSetting
    }

    @Composable
    fun floatSetting(key: String, defaultValue: Float): MutableState<Float> {
        return setting(key, defaultValue, Settings::getFloat, Settings::set)
    }
}
