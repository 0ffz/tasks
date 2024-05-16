package me.dvyy.tasks.ui.elements.app

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.state.LocalAppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    val app = LocalAppState
    val small by app.isSmallScreen.collectAsState()
    val modifier = if (small) Modifier else Modifier.heightIn(max = 45.dp)
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
