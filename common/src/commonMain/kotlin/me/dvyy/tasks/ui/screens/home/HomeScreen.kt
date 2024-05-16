package me.dvyy.tasks.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.ui.elements.week.WeekView

@Composable
fun HomeScreen() {
    Column(Modifier.padding(8.dp)) {
        WeekView()
    }
}
