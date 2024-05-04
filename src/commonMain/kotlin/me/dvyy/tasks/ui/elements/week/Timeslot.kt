package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun Timeslot(
    content: @Composable () -> Unit,
) {
    Surface {
        Row {
            content()
        }
    }
}
