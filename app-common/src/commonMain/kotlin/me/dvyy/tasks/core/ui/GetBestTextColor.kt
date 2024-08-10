package me.dvyy.tasks.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.getBestTextColor() = if (luminance() > 0.36f) Color.Black else Color.White
