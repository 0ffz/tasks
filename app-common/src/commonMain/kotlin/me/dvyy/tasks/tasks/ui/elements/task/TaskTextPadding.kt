package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.ui.LocalUIState

@Composable
fun TaskTextPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val ui = LocalUIState.current
    Box(
        modifier/*.height(ui.taskHeight)*/.padding(ui.taskTextPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}
