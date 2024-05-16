package me.dvyy.tasks.ui.elements.task

import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.model.Highlight

val Highlight.color
    get() = when (this) {
        Highlight.Unmarked -> Color.Transparent
        Highlight.Important -> Color.Red.copy(alpha = 0.5f)
        Highlight.InProgress -> Color.Yellow.copy(alpha = 0.5f)
    }
