package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.asList
import me.dvyy.tasks.model.database.AppDatabase
import me.dvyy.tasks.model.database.Projects
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppFileTree(
    tasks: TasksViewModel = koinViewModel(),
    db: AppDatabase = koinInject(),
    app: AppState = koinInject(),
) = Column(
    Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    val drawer = app.drawerState
    val scope = rememberCoroutineScope()
    fun closeDrawer() {
        scope.launch { drawer.close() }
    }
    Text("Calendar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

    fun file(
        layout: LayoutStructure.Single,
        onDropTask: ((TaskId) -> Unit)? = null,
        onDropList: ((ListId) -> Unit)? = null,
    ) = FileStructure.File(
        opensLayout = layout,
        onClick = ::closeDrawer,
        onStartDrag = ::closeDrawer,
        onDropTask = onDropTask,
        onDropList = onDropList,
    )

    FileList(
        listOf(
            file(LayoutStructure.Single.WeekView()),
            file(LayoutStructure.Single.WeekView(startAtToday = true, takeDays = 3)),
            file(LayoutStructure.Single.WeekView(startAtToday = true, takeDays = 1)),
        )
    )

    Spacer(Modifier.height(8.dp))

    Text("Projects", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

    FileList(
        buildList {
//            add(file(LayoutStructure.Single.Projects(staggered = true)))
//            add(file(LayoutStructure.Single.Projects(staggered = false)))
            add(file(LayoutStructure.Single.Projects(horizontal = true)))
            add(FileStructure.Element { HorizontalDivider() })

            //TODO add back project list
            val projects by tasks.projects.collectAsState()
            val scope = rememberCoroutineScope()
            projects.forEach { key ->
                add(file(LayoutStructure.Single.Project(key.id.asList()), onDropTask = {
                    scope.launch { db.mutate.childOf.move(it.uuid, toParent = key.id, atEnd = false) }
                }, onDropList = {
                    scope.launch { db.mutate.childOf.move(it.uuid, Projects.projectRoot, atChild = key.id) }
                }))
            }
        }
    )

    OutlinedButton(
        onClick = { tasks.createProject() },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Create project", maxLines = 1)
    }
}
