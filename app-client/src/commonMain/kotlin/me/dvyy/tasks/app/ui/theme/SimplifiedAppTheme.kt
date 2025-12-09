package me.dvyy.tasks.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlinx.serialization.Serializable

@Serializable
class SimplifiedAppTheme(
    val surface: String,
    val primary: String,
    val primaryContainer: String,
    val secondary: String,
    val secondaryContainer: String,
    val tertiary: String,
    val tertiaryContainer: String,
    val textLight: String,
    val textDark: String,
    val tint: String,
    val surfaceContainer: String,
    val outline: String,
    val outlineVariant: String,
) {
    @OptIn(ExperimentalStdlibApi::class)
    fun color(hex: String) = Color(hex.removePrefix("#").hexToLong() or 0xFF000000)
    fun bestText(hex: String) = if (color(hex).luminance() > 0.36f) color(textDark) else color(textLight)
}

enum class DarkModePref {
    DARK, LIGHT, AUTO
}

@Serializable
sealed interface TaskAppTheme {
    @Serializable
    sealed class Custom(
        val dark: SimplifiedAppTheme,
        val light: SimplifiedAppTheme = dark,
    ) : TaskAppTheme

    @Serializable
    data object JetbrainsLike : Custom(
        dark = SimplifiedAppTheme(
            surface = "#1E1F22",
            primary = "#6B9BFA",
            primaryContainer = "#3574F0",
            secondary = "#3574F0",
            secondaryContainer = "#3c4d6e",
            tertiary = "#FCD382",
            tertiaryContainer = "#CA8B0F",
            textLight = "#DFE1E5",
            textDark = "#000000",
            tint = "#DDDDDD",
            surfaceContainer = "#2B2D30",
            outline = "#43454A",
            outlineVariant = "#393B40"
        ),
        light = SimplifiedAppTheme(
            surface = "#FFFFFF",
            primary = "#315FBD",
            primaryContainer = "#3574F0",
            secondary = "#3574F0",
            secondaryContainer = "#3c4d6e",
            tertiary = "#C27D04",
            tertiaryContainer = "#FFAF0F",
            textLight = "#FFFFFF",
            textDark = "#000000",
            tint = "#222222",
            surfaceContainer = "#F7F8FA",
            outline = "#EBECF0",
            outlineVariant = "#DFE1E5"
        ),
    )

    @Serializable
    data object Material : TaskAppTheme
}
