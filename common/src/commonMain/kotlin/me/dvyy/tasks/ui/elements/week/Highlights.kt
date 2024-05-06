package me.dvyy.tasks.ui.elements.week

import androidx.compose.ui.graphics.Color

enum class Highlights(
    val color: Color
) {
    Unmarked(Color.Transparent),
    Important(Color.Red.copy(alpha = 0.5f)),
    InProgress(Color.Yellow.copy(alpha = 0.5f)),
//    Done(Color.Green.copy(alpha = 0.5f)),
}
