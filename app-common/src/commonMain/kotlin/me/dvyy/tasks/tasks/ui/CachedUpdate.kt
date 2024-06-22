package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach

// from https://stackoverflow.com/questions/76193549/how-to-use-database-as-source-of-truth-for-text-state-while-updating-textinput-q
@Composable
fun <T> CachedUpdate(
    value: T,
    onValueChanged: (T) -> Unit,
    debounceMillis: Long = 500,
    content: @Composable (MutableState<T>) -> Unit,
) {
    // this will run whenever a new value comes in from the outside (e.g. from DB)
    val cached = remember { mutableStateOf(value) }
    cached.value = value
    LaunchedEffect(onValueChanged, debounceMillis) {
        snapshotFlow { cached.value }.debounce(debounceMillis).onEach(onValueChanged).collect()
    }
    content(cached)
}
