package me.dvyy.tasks.ui.elements.sync

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.launch
import me.dvyy.tasks.state.LocalAppState

@Composable
fun SyncButton() {
    val scope = rememberCoroutineScope()
    val app = LocalAppState
    IconButton(
        onClick = {
            scope.launch {
                app.sync.sync()
            }
        },
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(1000))
        )
        val inProgress by app.sync.inProgress.collectAsState()
        val isError by app.sync.isError.collectAsState()
        val icon = remember(isError) {
            if (isError) Icons.Rounded.SyncProblem
            else Icons.Rounded.Sync
        }
        Icon(
            icon,
            contentDescription = "Sync",
            modifier = Modifier.rotate(if (inProgress) -rotation else 0f)
        )
    }
}
