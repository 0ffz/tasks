package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach

// from https://stackoverflow.com/questions/76193549/how-to-use-database-as-source-of-truth-for-text-state-while-updating-textinput-q
@Composable
fun <T> CachedUpdate(
    key: Any,
    value: T,
    onValueChanged: (T) -> Unit,
    debounceMillis: Long = 100,
    content: @Composable (cached: T, setCached: (T) -> Unit) -> Unit,
) {
    // this will run whenever a new value comes in from the outside (e.g. from DB)
    val cached = remember { mutableStateOf(value) }
    var awaitingPush by remember { mutableStateOf(false) }
    if (!awaitingPush) cached.value = value
    //TODO onValueChanged isn't considered immutable for some lambdas so this will fire repeatedly,
    // think about whether or not to include it here.
    // Specifically had issues with onPropertiesChanged
    LaunchedEffect(key, debounceMillis) {
        snapshotFlow { cached.value }
            .debounce(debounceMillis)
            .onEach {
                onValueChanged(it)
            }.collect()
    }
    content(cached.value) {
        awaitingPush = true
        cached.value = it
    }

    LaunchedEffect(key) {
        // On key change, just get the new data
        // otherwise we could accidentally start altering another list item when it gets added in at this item's index
        awaitingPush = false
        cached.value = value
        snapshotFlow { cached.value }
            .collectLatest {
                if (!awaitingPush) return@collectLatest
                delay(500)
                onValueChanged(it)
                awaitingPush = false
            }
    }
}
