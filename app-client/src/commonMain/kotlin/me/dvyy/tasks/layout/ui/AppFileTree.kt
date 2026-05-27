package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seyfarth.tablericons.outlined.Plus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.Projects
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButtonContainer
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonColumn
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun AppFileTree(
    onPromptDeleteProject: (ListId) -> Unit,
) = Box {
    Column(
        Modifier.padding(top = UI.padding.sm).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(UI.padding.md),
    ) {
        //TODO should not access from composable
        val db: AppDatabase by rememberInstance()
        val tasks: TasksViewModel by rememberViewModel()
        val app: AppState by rememberInstance()
        val drawer = app.drawerState
        val scope = rememberCoroutineScope()
        fun closeDrawer() {
            scope.launch { drawer.close() }
        }

        fun file(
            layout: ScreenDest,
            onDropTask: ((TaskId) -> Unit)? = null,
            onDropList: ((ListId) -> Unit)? = null,
        ) = FileStructure.File(
            opensLayout = layout,
            onClick = ::closeDrawer,
            onStartDrag = ::closeDrawer,
            onDropTask = onDropTask,
            onDropList = onDropList,
        )

        Column {
            Text("Calendar", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = UI.padding.md, vertical = UI.padding.sm))

            FileList(
                persistentListOf(
                    file(ScreenDest.Week()),
                    file(ScreenDest.Week(startAtToday = true, takeDays = 3)),
                    file(ScreenDest.Week(startAtToday = true, takeDays = 1)),
                ), onPromptDeleteProject
            )
        }

        ButtonColumn {
            Text("Projects", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = UI.padding.md, vertical = UI.padding.sm))

            FileList(
                buildList {
                    add(file(ScreenDest.Projects(horizontal = true)))
//                    add(FileStructure.Element { HorizontalDivider() })

                    //TODO add back project list
                    val projects by tasks.projects.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()
                    projects.forEach { key ->
                        add(file(ScreenDest.Project(key.id.asList()), onDropTask = {
                            scope.launch { db.mutate.childOf.move(it.uuid, toParent = key.id, atEnd = false) }
                        }, onDropList = {
                            scope.launch { db.mutate.childOf.move(it.uuid, Projects.projectRoot, atChild = key.id) }
                        }))
                    }
                }.toImmutableList(),
                onPromptDeleteProject
            )

            BoxButtonContainer(
                onClick = { tasks.createProject() },
                modifier = Modifier.fillMaxWidth(),
                tint = MaterialTheme.colorScheme.onSurface.fade()
            ) {
                LeadingIcon(icon = AppIcons.Plus, "Add") {
                    Text("Create project", maxLines = 1)
                }
            }
            Spacer(Modifier.height(UI.tabHeight * 1.5f))
        }
    }
}
