package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import me.dvyy.tasks.app.LogEntry
import me.dvyy.tasks.app.TrackingLogWriter
import me.dvyy.tasks.app.ui.UI
import org.koin.compose.koinInject

@Composable
fun LogsTab(
    logSource: TrackingLogWriter = koinInject(),
) {
    val logs = remember { mutableStateListOf<LogEntry>() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        logSource.logFlow.collect { logs.add(it) }
    }

    // Scroll to bottom
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.size - 1)
    }
    BoxedList(modifier = Modifier.padding(bottom = UI.padding.md)) {
        Box(modifier = Modifier.fillMaxWidth().padding(UI.padding.sm).fillMaxHeight()) {
            val horizontalScrollState = rememberScrollState()
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize().horizontalScroll(horizontalScrollState)) {
                items(logs) { log ->
                    Text(
                        text = log.message,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        softWrap = false
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState)
            )
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(horizontalScrollState)
            )
        }
    }
}