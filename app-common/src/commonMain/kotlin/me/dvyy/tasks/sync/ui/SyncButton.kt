package me.dvyy.tasks.sync.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.sync.data.SyncClient
import org.koin.compose.koinInject

@Composable
fun SyncButton(
    app: AppState = koinInject(),
    sync: SyncClient = koinInject(),
) {
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                sync.sync()
            }
        },
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(1000))
        )
        val inProgress by sync.inProgress.collectAsState()
        val isError by sync.isError.collectAsState()
        val icon = remember(isError) {
            if (isError) Icons.Outlined.SyncProblem
            else Icons.Outlined.Sync
        }
        Icon(
            icon,
            contentDescription = "Sync",
            modifier = Modifier.rotate(if (inProgress) -rotation else 0f)
        )
    }
}
