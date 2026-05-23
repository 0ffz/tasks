package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Surface(tonalElevation = 0.75f.dp, shape = MaterialTheme.shapes.medium) {
        Column {
            content()
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
    SettingItem(name, description, isLast, Modifier.clickable { onCheckedChange(!checked) }) {
        Switch(checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingButton(
    name: String,
    description: String? = null,
    isLast: Boolean = false,
    onClick: () -> Unit,
) {
    SettingItem(name, description, isLast, Modifier.clickable { onClick() }) {
    }
}

@Composable
fun SettingItem(
    name: String,
    description: String? = null,
    isLast: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(UI.padding.sm),
        modifier = modifier.padding(UI.padding.lg)
    ) {
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
    if (!isLast) TintedHorizontalDivider()
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