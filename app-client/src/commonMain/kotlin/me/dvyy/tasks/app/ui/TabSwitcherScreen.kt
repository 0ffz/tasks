package me.dvyy.tasks.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Plus
import dev.seyfarth.tablericons.outlined.X
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Location
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow

@Composable
fun TabSwitcherScreen(
    tabs: List<LayoutStructure>,
    activeTabIndex: Int,
    onNavigateToTab: (Int) -> Unit,
    onCloseTab: (Int) -> Unit,
    onOpenNewTab: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        itemsIndexed(tabs) { index, tab ->
            TabItem(
                title = { if (tab is LayoutStructure.Single) tab.tabLabel(selected = Location.TabList) },
                isSelected = index == activeTabIndex,
                onClick = { onNavigateToTab(index) },
                onClose = { onCloseTab(index) }
            )
        }
        item {
            TabItem({ Text("New tab") }, isSelected = false, onClick = onOpenNewTab, onClose = {}) {
                Icon(TablerIcons.Outlined.Plus, "New Tab", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun TabItem(
    title: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickableWithoutRipple(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                tonalElevation = UI.elevation.lv1,
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonRow {
                    Spacer(Modifier.width(UI.padding.sm))
                    Row(Modifier.weight(1f)) {
                        title()
                    }
                    BoxButton(onClick = { onClose() }) {
                        Icon(TablerIcons.Outlined.X, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )

            Surface {
                Box(Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}