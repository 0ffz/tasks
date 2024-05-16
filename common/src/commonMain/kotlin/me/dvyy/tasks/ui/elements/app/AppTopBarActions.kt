package me.dvyy.tasks.ui.elements.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import me.dvyy.tasks.state.LocalTimeState

@Composable
fun AppTopBarActions() {
    val time = LocalTimeState.current
    // Previous and next icon buttons
    FilledTonalIconButton(onClick = { time.previousWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
    }
    FilledTonalIconButton(onClick = { time.nextWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")
    }
}
