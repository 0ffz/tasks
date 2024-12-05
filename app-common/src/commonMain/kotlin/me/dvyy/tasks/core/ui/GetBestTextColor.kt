package me.dvyy.tasks.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun Color.getBestTextColor() = if (luminance() > 0.36f) Color.Black else MaterialTheme.colorScheme.onSurface
