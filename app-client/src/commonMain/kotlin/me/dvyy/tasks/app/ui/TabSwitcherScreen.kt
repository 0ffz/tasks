package me.dvyy.tasks.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition
import me.dvyy.tasks.layout.ui.layouts.LayoutTab

@Composable
fun TabSwitcherScreen(
    tabs: List<LayoutDefinition>,
    onNavigateToTab: (Int) -> Unit,
    onCloseTab: (Int) -> Unit,
    selectedTab: Int,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        itemsIndexed(tabs) { index, tab ->
            LayoutTab(selected = index == selectedTab, tab) //TODO is selected
        }
    }
}
