package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.dvyy.tasks.app.ui.PreferencesViewModel
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.model.Highlight.Type

@Serializable
data class SerializableColorScheme(
    val light: List<String>,
    val dark: List<String>,
): ColorScheme {
    @Transient
    val lightInts = light.map { it.toInt(16) }

    @Transient
    val darkInts = dark.map { it.toInt(16) }

    override val lightAndDark: Boolean = dark.isNotEmpty()

    override fun color(highlight: Highlight): Color {
        return if (highlight.isLight) when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(lightInts[0])
            Type.Green -> Color(lightInts[1])
            Type.Yellow -> Color(lightInts[2])
            Type.Blue -> Color(lightInts[3])
            Type.Magenta -> Color(lightInts[4])
            Type.Cyan -> Color(lightInts[5])
            Type.Light -> Color(lightInts[6])
        }
        else when (highlight.type) {
            Type.Unmarked -> Color.Transparent
            Type.Red -> Color(darkInts[0])
            Type.Green -> Color(darkInts[1])
            Type.Yellow -> Color(darkInts[2])
            Type.Blue -> Color(darkInts[3])
            Type.Magenta -> Color(darkInts[4])
            Type.Cyan -> Color(darkInts[5])
            Type.Light -> Color(darkInts[6])
        }
    }
}
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
    @Composable
    get() {
        val prefs = koinViewModel<PreferencesViewModel>()
        val theme by prefs.deserializedTheme.collectAsState()
        if (this == Highlight.Unmarked) return Color.Transparent
        val alpha = if (isLight || theme.lightAndDark) 1f else 0.5f
        return theme.color(this).copy(alpha = alpha)
    }
