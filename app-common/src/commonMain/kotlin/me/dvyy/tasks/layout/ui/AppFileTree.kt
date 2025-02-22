package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.VaultViewModel
import me.dvyy.tasks.layout.ui.layouts.TabHeader
import me.dvyy.tasks.layout.ui.layouts.TabLabel
import me.dvyy.tasks.tree.ui.FileList
import me.dvyy.tasks.tree.ui.FileStructure
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppFileTree(
    vault: VaultViewModel = koinViewModel(),
//    tasks: TasksViewModel = koinViewModel(),
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

    fun file(layout: LayoutStructure.Single) = FileStructure.File(
        opensLayout = layout,
        onClick = ::closeDrawer,
        onStartDrag = ::closeDrawer,
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
            add(file(LayoutStructure.Single.Projects(staggered = true)))
            add(file(LayoutStructure.Single.Projects(staggered = false)))
            add(file(LayoutStructure.Single.Projects(horizontal = true)))
            add(FileStructure.Element { HorizontalDivider() })

            val projects by vault.fileTree.collectAsState()
//            val projects by tasks.projects.collectAsState()

            projects.forEach { entry ->
                add(entry)
            }
        }
    )

    OutlinedButton(
        onClick = { /*tasks.createProject()*/ },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Create project")
    }
    OutlinedButton(
        onClick = { vault.vault.index() },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Reindex")
    }
}
