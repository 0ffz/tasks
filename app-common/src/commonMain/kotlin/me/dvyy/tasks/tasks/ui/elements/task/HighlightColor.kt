package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.Highlight.Type

interface ColorScheme {
    val lightAndDark: Boolean
    fun color(highlight: Highlight): Color
}

object ArgonautColorScheme : ColorScheme {
    override val lightAndDark = true

    override fun color(highlight: Highlight): Color {
        return if (highlight.isLight) when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xFF2740)
            Type.Yellow -> Color(0xFFD242)
            Type.Green -> Color(0xABE15B)
            Type.Blue -> Color(0x0092FF)
            Type.Magenta -> Color(0x9A5FEB)
            Type.Cyan -> Color(0x67FFF0)
            Type.Light -> Color(0xFFFFFF)
        }
        else when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xFF000F)
            Type.Yellow -> Color(0xFFB900)
            Type.Green -> Color(0x8CE10B)
            Type.Blue -> Color(0x008DF8)
            Type.Magenta -> Color(0x6D43A6)
            Type.Cyan -> Color(0x00D8EB)
            Type.Light -> Color(0xD0D0D0)
        }
    }
}

val DefaultColorScheme = ArgonautColorScheme

val Highlight.color: Color
    get() {
        if (this == Highlight.Unmarked) return Color.Transparent
        val alpha = if (isLight || DefaultColorScheme.lightAndDark) 1f else 0.5f
        return DefaultColorScheme.color(this).copy(alpha = alpha)
    }
