package me.dvyy.tasks.app.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import me.dvyy.tasks.model.serializers.AppFormats

class LocalPreferencesRepository(
    val settings: Settings,
) {
    val debounceMillis = 500L

    inline fun <T> setting(
        scope: CoroutineScope,
        key: String,
        defaultValue: T,
        crossinline read: (Settings, String, T) -> T,
        crossinline write: (Settings, String, T) -> Unit,
    ): MutableStateFlow<T> {
        val cachedSetting = MutableStateFlow(read(settings, key, defaultValue))

        scope.launch(Dispatchers.Default) {
            cachedSetting.debounce(debounceMillis).collect {
                write(settings, key, it)
            }
        }

        return cachedSetting
    }

    fun <T> serializable(
        scope: CoroutineScope,
        key: String,
        defaultValue: T,
        serializer: kotlinx.serialization.KSerializer<T>,
    ): MutableStateFlow<T> {
        return setting(scope, key, defaultValue, { settings, key, defaultValue ->
            val json = settings.getStringOrNull(key) ?: return@setting defaultValue
            runCatching { AppFormats.json.decodeFromString(serializer, json) }
                .getOrDefault(defaultValue)
        }, { settings, key, value ->
            settings[key] = AppFormats.json.encodeToString(serializer, value)
        })
    }

    inline fun <reified T> serializable(
        scope: CoroutineScope,
        key: String,
        defaultValue: T,
    ): MutableStateFlow<T> = serializable(scope, key, defaultValue, serializer<T>())


    fun float(scope: CoroutineScope, key: String, defaultValue: Float): MutableStateFlow<Float> =
        setting(scope, key, defaultValue, Settings::getFloat, Settings::set)

    fun double(scope: CoroutineScope, key: String, defaultValue: Double): MutableStateFlow<Double> =
        setting(scope, key, defaultValue, Settings::getDouble, Settings::set)

    fun int(scope: CoroutineScope, key: String, defaultValue: Int): MutableStateFlow<Int> =
        setting(scope, key, defaultValue, Settings::getInt, Settings::set)

    fun long(scope: CoroutineScope, key: String, defaultValue: Long): MutableStateFlow<Long> =
        setting(scope, key, defaultValue, Settings::getLong, Settings::set)

    fun boolean(scope: CoroutineScope, key: String, defaultValue: Boolean): MutableStateFlow<Boolean> =
        setting(scope, key, defaultValue, Settings::getBoolean, Settings::set)

    fun string(scope: CoroutineScope, key: String, defaultValue: String): MutableStateFlow<String> =
        setting(scope, key, defaultValue, Settings::getString, Settings::set)

}
