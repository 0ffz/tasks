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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.state.LocalAppState

@Composable
fun SyncButton(snackbarHostState: SnackbarHostState) {
    var inProgress by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val app = LocalAppState
    var isError by remember { mutableStateOf(false) }
    IconButton(
        onClick = {
            if (inProgress) return@IconButton
            inProgress = true
            isError = false
            coroutineScope.launch {
                try {
                    app.sync.sync()
                } catch (e: IOException) {
                    isError = true
                    e.printStackTrace()
                    launch {
                        snackbarHostState
                            .showSnackbar("Error syncing: ${e.message ?: "Unknown error"}", withDismissAction = true)
                    }
                } finally {
                    inProgress = false
                }
            }
        },
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(1000))
        )
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
