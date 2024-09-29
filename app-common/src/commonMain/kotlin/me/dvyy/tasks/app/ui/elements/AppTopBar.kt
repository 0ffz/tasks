package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
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
            if(!UI.isSmall) AppTopBarActions()
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
}
