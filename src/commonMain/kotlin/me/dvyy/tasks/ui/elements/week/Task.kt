package me.dvyy.tasks.ui.elements.week

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Task(
    name: String,
) {
    Card {
        Button(onClick = { /*TODO*/ }) {
            Text(name)
        }
    }
}
