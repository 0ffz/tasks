package me.dvyy.tasks.tasks.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach

// from https://stackoverflow.com/questions/76193549/how-to-use-database-as-source-of-truth-for-text-state-while-updating-textinput-q
@Composable
fun <T> CachedUpdate(
    key: Any,
    value: T,
    onValueChanged: (T) -> Unit,
    debounceMillis: Long = 300,
    content: @Composable (cached: T, setCached: (T) -> Unit) -> Unit,
) {
    // this will run whenever a new value comes in from the outside (e.g. from DB)
    val cached = remember(key) { mutableStateOf(value) }
    var awaitingPush by remember { mutableStateOf(false) }
//    if (!awaitingPush) cached.value = value
    //TODO onValueChanged isn't considered immutable for some lambdas so this will fire repeatedly,
    // think about whether or not to include it here.
    // Specifically had issues with onPropertiesChanged
    LaunchedEffect(key, debounceMillis) {
        awaitingPush = false
        snapshotFlow { cached.value }
            .drop(1)
            .onEach {
                awaitingPush = true
            }
            .debounce(debounceMillis)
            .collectLatest {
                onValueChanged(it)
                awaitingPush = false
            }
    }

    LaunchedEffect(key, value) {
        if (!awaitingPush) cached.value = value
    }

    content(cached.value) {
        cached.value = it
    }
}
