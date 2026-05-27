package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    val responsive = LocalUIState.current
    val modifier = if (responsive.smallTopBar) Modifier.height(UI.size.xl) else Modifier
    TopAppBar(
        title = {
            ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                AppTopBarTitle()
            }
        },
        navigationIcon = {
            AppDrawerIconButton()
        },
        actions = {
            if (!UI.isSmall) AppTopBarActions()
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    Icon(
        Icons.Outlined.ChecklistRtl,
        contentDescription = "App Icon",
        modifier = modifier,
        tint = MaterialTheme.colorScheme.primary,
    )
}
