package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import me.dvyy.tasks.core.ui.components.DesktopHorizontalScrollbar
import me.dvyy.tasks.core.ui.components.DesktopVerticalScrollbar
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
            DesktopVerticalScrollbar(
                listState, Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            )
            DesktopHorizontalScrollbar(
                horizontalScrollState, Modifier.align(Alignment.BottomStart).fillMaxWidth(),
            )
        }
    }
}