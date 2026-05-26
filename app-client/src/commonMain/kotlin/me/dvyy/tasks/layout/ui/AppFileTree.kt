package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.Projects
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButtonProps
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.viewmodel.rememberViewModel

@Composable
fun AppFileTree() = Box {
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
                listOf(
                    file(ScreenDest.Week()),
                    file(ScreenDest.Week(startAtToday = true, takeDays = 3)),
                    file(ScreenDest.Week(startAtToday = true, takeDays = 1)),
                )
            )
        }

        Column {
            Text("Projects", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = UI.padding.md, vertical = UI.padding.sm))

            FileList(
                buildList {
                    add(file(ScreenDest.Projects(horizontal = true)))
                    add(FileStructure.Element { HorizontalDivider() })

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
                }
            )

            BoxButton(
                onClick = { tasks.createProject() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                properties = BoxButtonProps(innerPadding = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Text("Create project", maxLines = 1)
            }
            Spacer(Modifier.height(UI.tabHeight * 1.5f))
        }
    }
}
