package me.dvyy.tasks.app.ui.elements

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.LocalUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    val responsive = LocalUIState.current
    val modifier = if (responsive.smallTopBar) Modifier.heightIn(max = 45.dp) else Modifier
    CenterAlignedTopAppBar(
        title = { AppTopBarTitle() },
        navigationIcon = {
            AppDrawerIconButton()
        },
        actions = {
            AppTopBarActions()
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
//    TopAppBar(
//        title = { Text("Tasks") },
//        actions = {
//            AppDrawerIconButton()
//        }
//    )
}
