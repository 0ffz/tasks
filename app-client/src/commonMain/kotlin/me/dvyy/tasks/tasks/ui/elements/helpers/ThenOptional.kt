package me.dvyy.tasks.tasks.ui.elements.helpers

import androidx.compose.ui.Modifier

inline fun Modifier.optional(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) then(Modifier.modifier()) else this
