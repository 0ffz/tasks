package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.layout.ui.LayoutStructure

@Composable
fun EmptyLayout(
    onLayoutUpdate: (LayoutStructure) -> Unit,
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
        TextButton(onClick = { onLayoutUpdate(LayoutStructure.Single.WeekView()) }) {
            Text("Open week view")
        }
    }
    DropTarget(LayoutStructure.Empty, onLayoutUpdate)
}