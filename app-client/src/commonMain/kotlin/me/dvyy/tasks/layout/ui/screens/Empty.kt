package me.dvyy.tasks.layout.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.layout.ui.screens.builder.screen

fun emptyScreen() = screen(
    tabLabel = { AnnotatedString("New tab") }
) {
    EmptyScreen(onLayoutChange)
}

@Composable
private fun EmptyScreen(
    onLayoutUpdate: (LayoutDefinition) -> Unit,
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No tab is open",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { onLayoutUpdate(LayoutDefinition.of(ScreenDest.Week())) }) {
            Text("Open week view")
        }
    }
//    DropTarget(LayoutStructure.Empty, onLayoutUpdate, splitTargets = false)
}