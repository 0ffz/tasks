package me.dvyy.tasks.app.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HorizontalSplit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.layout.ui.LayoutViewModel

@Composable
fun BottonBarFAB(
    layout: LayoutViewModel = koinViewModel(),
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        AnimatedVisibility(expanded) {
            val buttons by layout.layoutButtonLocations.collectAsState()
            buttons.bottom.forEach { button ->
                val selected by layout.bottomBar.collectAsState()
                val isSelected = button.structure == selected
                val color by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)

                SmallFloatingActionButton(
                    onClick = {
                        layout.bottomBar.update {
                            if (isSelected) LayoutStructure.Empty
                            else button.structure
                        }
                    },
                    containerColor = color
                ) {
                    Icon(button.icon, button.displayName)
                }
            }
        }
        SmallFloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            Icon(Icons.Outlined.HorizontalSplit, "View")
        }
    }
}
