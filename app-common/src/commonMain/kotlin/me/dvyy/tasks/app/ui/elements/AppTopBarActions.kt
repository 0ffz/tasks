package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import me.dvyy.tasks.app.ui.TimeViewModel
import me.dvyy.tasks.di.koinViewModel

@Composable
fun AppTopBarActions(time: TimeViewModel = koinViewModel()) = Row {
    // Previous and next icon buttons
    FilledTonalIconButton(onClick = { time.previousWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
    }
    FilledTonalIconButton(onClick = { time.nextWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")
    }
}
