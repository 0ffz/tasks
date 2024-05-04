package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DayList(
) {
    Column() {
        repeat(8) {
            Timeslot {
                Task("Task $it")
            }
            HorizontalDivider()
        }
    }
}
