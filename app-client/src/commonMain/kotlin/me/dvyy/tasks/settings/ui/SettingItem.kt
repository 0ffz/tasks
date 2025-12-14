package me.dvyy.tasks.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.layout.ui.layouts.TintedHorizontalDivider

@Composable
fun BoxedList(
    title: String? = null,
    description: String? = null,
    content: @Composable () -> Unit,
) = Column(
    verticalArrangement = Arrangement.spacedBy(UI.padding.md),
    modifier = Modifier.padding(top = UI.padding.md)
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
    Surface(tonalElevation = UI.elevation.lv1, shape = MaterialTheme.shapes.medium) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    description: String? = null,
    isLast: Boolean = false,
    content: @Composable () -> Unit,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(UI.padding.sm),
        modifier = Modifier.padding(UI.padding.lg)
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