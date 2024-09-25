package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.AllProjectsView
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.WeekView
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import me.dvyy.tasks.utils.loadedOrNull

@Composable
fun AppFileTree(
    tasks: TasksViewModel = koinViewModel(),
) = Column(
    Modifier.padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text("Calendar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

    FileList(
        listOf(
            FileStructure.File(
                "Week view",
                opensLayout = { WeekView() },
                icon = AppIcons.CalendarViewWeek
            ),
            FileStructure.File(
                "3-day view",
                opensLayout = { WeekView(startAtToday = true, takeDays = 3) },
                icon = AppIcons.CalendarViewDay
            ),
            FileStructure.File(
                "Today",
                opensLayout = { WeekView(startAtToday = true, takeDays = 1) },
                icon = AppIcons.CalendarToday
            ),
        )
    )

    Spacer(Modifier.height(8.dp))

    Text("Projects", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

    FileList(
        buildList {
            add(
                FileStructure.File(
                    "All staggered",
                    opensLayout = { AllProjectsView(staggered = true) },
                    icon = AppIcons.Dashboard
                )
            )
            add(
                FileStructure.File(
                    "All grid",
                    opensLayout = { AllProjectsView(staggered = false) },
                    icon = AppIcons.GridView
                )
            )
            val projects by tasks.projects.collectAsState()

            projects.forEach { key ->
                val propLoadable by tasks.getListProperties(key).collectAsState()
                val props = propLoadable.loadedOrNull() ?: return
                val icon = when (props.displayName) {
                    "Inbox" -> AppIcons.Inbox
                    else -> AppIcons.Description
                }
                add(
                    FileStructure.File(
                        props.displayName ?: "Unnamed",
                        icon = icon,
                        opensLayout = {
                            Project(
                                key = key,
                                properties = propLoadable
                            )
                        },
                    )
                )
            }
        }
    )

    TextButton(
        onClick = { tasks.createProject() },
    ) {
        Text("Create project")
    }
}
