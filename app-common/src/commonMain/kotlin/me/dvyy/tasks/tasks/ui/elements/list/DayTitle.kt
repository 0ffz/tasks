package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

sealed class TaskListKey {
    @Immutable
    @Serializable
    data class Date(val date: LocalDate) : TaskListKey() {
        override fun toString() = date.toString()
    }

    @Immutable
    @Serializable
    data class Project(val name: String) : TaskListKey() {
        override fun toString() = name
    }
}

@Composable
fun TaskListTitle(
    title: TaskListKey,
    colored: Boolean,
    loading: Boolean = false,
    showDivider: Boolean = true,
) {
    val color =
        if (colored) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        Modifier.padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        when (title) {
            is TaskListKey.Date -> {
                val date = title.date
                Text(
                    "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}",
                    Modifier.weight(1f, true),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                )
                Text(
                    date.dayOfWeek.name.lowercase().capitalize().take(3),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    color = color.copy(alpha = 0.6f)
                )
            }

            is TaskListKey.Project -> {
                Text(
                    title.name,
                    Modifier.weight(1f, true),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                )
            }
        }
    }
    if (showDivider) Box {
        if (!loading) HorizontalDivider(
            thickness = 2.dp,
            color = color
        )
        AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
        }
    }
}
