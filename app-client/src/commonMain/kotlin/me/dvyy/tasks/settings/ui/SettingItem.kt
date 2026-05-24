package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.layout.ui.layouts.TintedHorizontalDivider

@Composable
fun BoxedList(
    title: String? = null,
    description: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Column(
    verticalArrangement = Arrangement.spacedBy(UI.padding.md),
    modifier = modifier.padding(top = UI.padding.md)
) {
    title?.let {
        Text(
            it,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
    description?.let {
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.fade(0.8f),
        )
    }
    Surface(tonalElevation = 0.75f.dp, shape = UI.shapes.rounded) {
        Column {
            SubcomposeLayout { constraints ->
                val mainMeasurables = subcompose("content", content)
                val dividerMeasurables = List(mainMeasurables.size - 1) {
                    subcompose("divider$it") { TintedHorizontalDivider() }
                }

                val mainPlaceables = mainMeasurables.map { it.measure(constraints) }
                val dividerPlaceables = dividerMeasurables.map { it.map { m -> m.measure(constraints) } }

                val totalHeight = mainPlaceables.sumOf { it.height } + dividerPlaceables.flatten().sumOf { it.height }

                layout(constraints.maxWidth, totalHeight) {
                    var y = 0
                    mainPlaceables.forEachIndexed { index, placeable ->
                        placeable.placeRelative(0, y)
                        y += placeable.height
                        if (index < dividerPlaceables.size) {
                            dividerPlaceables[index].forEach { d -> d.placeRelative(0, y); y += d.height }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingToggle(
    name: String,
    description: String? = null,
    isLast: Boolean = false,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingItem(name, description, Modifier.clickable { onCheckedChange(!checked) }) {
        Switch(checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingButton(
    name: String,
    description: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    isLast: Boolean = false,
    onClick: () -> Unit,
) {
    SettingItem(name, description, Modifier.clickable { onClick() }, leadingIcon = leadingIcon) {}
}

@Composable
fun SettingItem(
    name: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(UI.padding.sm),
        modifier = modifier.padding(UI.padding.lg)
    ) {
        if (leadingIcon != null) {
            Box(modifier = Modifier.align(Alignment.CenterVertically)) { leadingIcon() }
            Spacer(Modifier.width(UI.padding.md))
        }
        Column(Modifier.weight(1f).align(Alignment.CenterVertically)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            description?.let {
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.fade(0.8f)
                )
            }
        }
        Spacer(Modifier.width(UI.padding.sm))
        content()
    }
}

@Composable
fun SettingsButtonGroup(
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(UI.padding.md)
    ) {
        content()
    }
}

@Composable
fun MultilineSettingItem(
    name: String? = null,
    description: String? = null,
    isLast: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(UI.padding.lg)) {
        name?.let { Text(name, style = MaterialTheme.typography.titleMedium) }
        description?.let {
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.fade(0.8f)
            )
        }
        Spacer(Modifier.height(UI.padding.sm))
        content()
    }
    if (!isLast) TintedHorizontalDivider()
}