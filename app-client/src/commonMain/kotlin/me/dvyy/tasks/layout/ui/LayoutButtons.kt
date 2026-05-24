package me.dvyy.tasks.layout.ui

import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.CalendarWeek
import dev.seyfarth.tablericons.outlined.FolderOpen

object LayoutButtons {
//    val projects = LayoutButton(
//        "Projects", "projects",
//        LayoutStructure.Tabbed(
//            name = "Projects",
//            tabs = listOf(LayoutStructure.Single.Projects(staggered = false)),
//            selected = 0,
//        ),
//        icon = AppIconsMirrored.ViewList
//    )

    val fileTree = LayoutButton(
        "File tree", "file_tree",
        LayoutStructure.Single.FileTree,
        icon = TablerIcons.Outlined.FolderOpen
    )

    val weeklyTasks = LayoutButton(
        "Weekly", "weekly_tasks",
        LayoutStructure.Single.WeekView(),
        icon = TablerIcons.Outlined.CalendarWeek
    )
}


