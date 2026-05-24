package me.dvyy.tasks.sync.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.CloudDownload
import dev.seyfarth.tablericons.outlined.Refresh
import dev.seyfarth.tablericons.outlined.RefreshAlert
import dev.seyfarth.tablericons.outlined.RefreshOff
import me.dvyy.tasks.app.data.UpdateViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun SyncIndicator() {
    val sync: SyncViewModel by rememberViewModel()
    val state by sync.syncState.collectAsState()
    val icon = when (state) {
        is SyncUiState.Error -> TablerIcons.Outlined.RefreshAlert
        is SyncUiState.Disabled -> TablerIcons.Outlined.RefreshOff
        else -> TablerIcons.Outlined.Refresh
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

@Composable
fun UpdateIndicator() {
    val updates: UpdateViewModel by rememberViewModel()
    val updateUrl by updates.updateUrl.collectAsState()
    if (updateUrl != null) {
        BoxButton(
            onClick = {
                TODO()
//                dialogs.showScreen(AppScreen.Settings(SettingsTab.Update))
            },
        ) {
            Icon(TablerIcons.Outlined.CloudDownload, contentDescription = "App update available")
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
