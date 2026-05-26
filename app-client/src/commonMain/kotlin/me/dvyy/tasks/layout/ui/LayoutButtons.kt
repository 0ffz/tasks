package me.dvyy.tasks.layout.ui

import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.CalendarWeek
import dev.seyfarth.tablericons.outlined.FolderOpen
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest

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
        ScreenDest.Week(), //FIXME filetree
        icon = TablerIcons.Outlined.FolderOpen
    )

    val weeklyTasks = LayoutButton(
        "Weekly", "weekly_tasks",
        ScreenDest.Week(),
        icon = TablerIcons.Outlined.CalendarWeek
    )
}


