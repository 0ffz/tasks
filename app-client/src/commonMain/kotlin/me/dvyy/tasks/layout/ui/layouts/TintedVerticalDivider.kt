package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun TintedHorizontalDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier.alpha(0.6f))
}

@Composable
fun TintedVerticalDivider(modifier: Modifier = Modifier) {
    VerticalDivider(modifier.alpha(0.6f))
}