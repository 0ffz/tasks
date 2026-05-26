package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppTopBarActions() = Row {
    PlatformSpecificTopBarActions()
}

@Composable
expect fun PlatformSpecificTopBarActions()


@Composable
expect fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit)

