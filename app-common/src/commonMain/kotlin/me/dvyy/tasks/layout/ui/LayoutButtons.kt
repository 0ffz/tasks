package me.dvyy.tasks.layout.ui

import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.FolderOpen
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.AppIconsMirrored

object LayoutButtons {
    val projects = LayoutButton(
        "Projects", "projects",
        LayoutStructure.Tabbed(
            name = "Projects",
            tabs = listOf(LayoutStructure.Single.Projects(staggered = false)),
            selected = 0,
        ),
        icon = AppIconsMirrored.ViewList
    )

    val fileTree = LayoutButton(
        "File tree", "file_tree",
        LayoutStructure.Single.FileTree,
        icon = AppIcons.FolderOpen
    )

    val weeklyTasks = LayoutButton(
        "Weekly", "weekly_tasks",
        LayoutStructure.Single.WeekView(),
        icon = AppIcons.CalendarViewWeek
    )
}


