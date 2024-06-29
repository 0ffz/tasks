package me.dvyy.tasks.sync.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.SyncState
import kotlin.time.Duration.Companion.seconds

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
    var syncState: SyncState by remember { mutableStateOf(SyncState.UnSynced) }
    LaunchedEffect(Unit) {
        sync.syncState.onEach {
            syncState = it
        }.debounce(3.seconds).collect {
            syncState = SyncState.UnSynced
        }
    }

    Crossfade(syncState) {
        val icon = when (it) {
            is SyncState.Error -> Icons.Outlined.SyncProblem
            is SyncState.Success -> Icons.Outlined.PublishedWithChanges
            else -> Icons.Outlined.Sync
        }
        Icon(
            icon,
            contentDescription = "Sync",
            modifier = Modifier.rotate(if (syncState is SyncState.InProgress) -rotation else 0f)
        )
    }
}
