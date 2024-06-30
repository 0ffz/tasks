package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.Highlight.Type

interface ColorScheme {
    val lightAndDark: Boolean
    fun color(highlight: Highlight): Color
}

object EspressoLibreColorScheme : ColorScheme {
    override val lightAndDark = true


    override fun color(highlight: Highlight): Color {
        return if (highlight.isLight) when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xEF2929)
            Type.Green -> Color(0x9AFF87)
            Type.Yellow -> Color(0xFFFB5C)
            Type.Blue -> Color(0x43A8ED)
            Type.Magenta -> Color(0xFF818A)
            Type.Cyan -> Color(0x34E2E2)
            Type.Light -> Color(0xEEEEEC)
        }
        else when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xCC0000)
            Type.Green -> Color(0x1A921C)
            Type.Yellow -> Color(0xF0E53A)
            Type.Blue -> Color(0x0066FF)
            Type.Magenta -> Color(0xC5656B)
            Type.Cyan -> Color(0x06989A)
            Type.Light -> Color(0xD3D7CF)
        }
    }
}
object ArgonautColorScheme : ColorScheme {
    override val lightAndDark = true

    override fun color(highlight: Highlight): Color {
        return if (highlight.isLight) when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xFF2740)
            Type.Green -> Color(0xABE15B)
            Type.Yellow -> Color(0xFFD242)
            Type.Blue -> Color(0x0092FF)
            Type.Magenta -> Color(0x9A5FEB)
            Type.Cyan -> Color(0x67FFF0)
            Type.Light -> Color(0xFFFFFF)
        }
        else when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(0xFF000F)
            Type.Green -> Color(0x8CE10B)
            Type.Yellow -> Color(0xFFB900)
            Type.Blue -> Color(0x008DF8)
            Type.Magenta -> Color(0x6D43A6)
            Type.Cyan -> Color(0x00D8EB)
            Type.Light -> Color(0xD0D0D0)
        }
    }
}

val DefaultColorScheme = EspressoLibreColorScheme

val Highlight.color: Color
    get() {
        if (this == Highlight.Unmarked) return Color.Transparent
        val alpha = if (isLight || DefaultColorScheme.lightAndDark) 1f else 0.5f
        return DefaultColorScheme.color(this).copy(alpha = alpha)
    }
