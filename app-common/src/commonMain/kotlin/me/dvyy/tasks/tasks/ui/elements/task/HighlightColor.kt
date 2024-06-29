package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.model.Highlight
import org.kodein.emoji.Emoji
import org.kodein.emoji.symbols.punctuation.ExclamationMark
import org.kodein.emoji.travel_places.transport_ground.Construction

// A400 line on https://materialui.co/colors
private const val alpha = 0.5f
val Highlight.color
    get() = when (this) {
        Highlight.Unmarked -> Color.Transparent
        Highlight.Important -> Color(0xFF1744).copy(alpha = alpha)
        Highlight.InProgress -> Color(0xFFEA00).copy(alpha = alpha)
        Highlight.Orange -> Color(0xFF9100).copy(alpha = alpha)
        Highlight.Green -> Color(0x76FF03).copy(alpha = alpha)
        Highlight.Blue -> Color(0x2979FF).copy(alpha = alpha)
        Highlight.Purple -> Color(0x651FFF).copy(alpha = alpha)
    }

val Highlight.emoji
    get() = when (this) {
        Highlight.Important -> Emoji.ExclamationMark
        Highlight.Orange -> Emoji.Construction
        else -> null
    }
