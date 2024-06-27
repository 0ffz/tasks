package me.dvyy.tasks.sync.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.SyncState

@Composable
fun SyncStatusIcon(
    sync: SyncViewModel = koinViewModel(),
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000))
    )
    val syncState by sync.syncState.collectAsState()
    val isError = syncState == SyncState.Error
    val inProgress = syncState == SyncState.InProgress
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
