package me.dvyy.tasks.sync.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.SyncDisabled
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SyncIndicator(
    sync: SyncViewModel = koinViewModel(),
) {
    val state by sync.syncState.collectAsState()
    val icon = when (state) {
        is SyncUiState.Error -> Icons.Outlined.SyncProblem
        is SyncUiState.Disabled -> Icons.Outlined.SyncDisabled
        else -> Icons.Outlined.Sync
    }
    Crossfade(state) { state ->
        val color = if (state is SyncUiState.Connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        BoxButton(
            onClick = { sync.toggleSync() },
        ) {
            Icon(icon, contentDescription = "Sync", tint = color)
        }
    }
}

//@Composable
//fun SyncStatusIcon(
//    sync: SyncViewModel = koinViewModel(),
//) {
//    val infiniteTransition = rememberInfiniteTransition()
//    val rotation by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(tween(1000))
//    )
//    var syncState: SyncUiState by remember { mutableStateOf(SyncUiState.UnSynced) }
//    LaunchedEffect(Unit) {
//        sync.syncState.onEach {
//            syncState = it
//        }.debounce(2.seconds).collect {
//            syncState = SyncUiState.UnSynced
//        }
//    }
//
//    Crossfade(syncState) {
//        val icon = when (it) {
//            is SyncUiState.Error -> Icons.Outlined.SyncProblem
//            is SyncUiState.Success -> Icons.Outlined.PublishedWithChanges
//            else -> Icons.Outlined.Sync
//        }
//        Icon(
//            icon,
//            contentDescription = "Sync",
//            modifier = Modifier.rotate(if (syncState is SyncUiState.InProgress) -rotation else 0f)
//        )
//    }
//}
