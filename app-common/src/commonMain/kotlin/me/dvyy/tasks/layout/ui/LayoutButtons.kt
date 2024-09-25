package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.AppIconsMirrored
import me.dvyy.tasks.tasks.ui.elements.list.AllProjectsView
import me.dvyy.tasks.tasks.ui.elements.list.WeekView

object LayoutButtons {
    val projects = LayoutButton(
        "Projects", "projects",
        LayoutStructure.Tabbed(
            name = "Projects",
            tabs = listOf(
                LayoutStructure.Tab(
                    "All",
                    LayoutStructure.Single {
                        AllProjectsView(staggered = false, modifier = Modifier.fillMaxHeight())
                    }
                ),
            ),
            selected = 0,
        ),
        icon = AppIconsMirrored.ViewList
    )

    val fileTree = LayoutButton(
        "File tree", "file_tree",
        LayoutStructure.Single {
            AppFileTree()
        },
        icon = AppIcons.FolderOpen
    )

    val weeklyTasks = LayoutButton(
        "Weekly", "weekly_tasks",
        LayoutStructure.Single {
            WeekView()
        },
        icon = AppIcons.CalendarViewWeek
    )
}


