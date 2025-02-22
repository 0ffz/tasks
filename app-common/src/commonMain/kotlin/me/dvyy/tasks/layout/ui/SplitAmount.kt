package me.dvyy.tasks.layout.ui

import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable
import me.dvyy.tasks.utils.DpSerializer

@Serializable
sealed interface SplitAmount {
    @Serializable
    data class Fixed(val value: @Serializable(with = DpSerializer::class) Dp) : SplitAmount

    @Serializable
    data class Percent(val value: Float) : SplitAmount
}
