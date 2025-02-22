package me.dvyy.tasks.notes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.markdownTypography
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.VaultViewModel
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.tasks.ui.elements.list.AppScreen
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.views.data.EditView
import me.dvyy.tasks.views.data.MarkdownView
import me.dvyy.tasks.views.data.NoteView
import org.dizitart.kno2.documentOf
import org.dizitart.no2.collection.Document
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FrontMatterListItem(value: String) {
    // outlined chip with value
    InputChip(
        selected = false,
        onClick = {},
        label = { Text(value, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    )
}

@Composable
fun FrontMatterIcon(value: Any) {
    val icon = when (value) {
        is List<*> -> Icons.AutoMirrored.Outlined.List
        is Boolean -> AppIcons.CheckBox
        else -> Icons.AutoMirrored.Outlined.Segment
    }
    Icon(icon, "Key icon")
}

@Composable
fun NoteFrontMatter(
    frontMatter: Document,
    updateFrontMatter: (Document) -> Unit,
) {
    val propsLength = UI.propsLength
    frontMatter.forEach {
        Row(
            Modifier.padding(UI.padding.sm).height(UI.propsRowHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UI.padding.sm)
        ) {
            val key = it.first
            val value = it.second
//            Text(value::class.simpleName.toString())
            FrontMatterIcon(value)
            Box(Modifier.width(propsLength)) {
                Text(key)
            }
            when (value) {
                is List<*> -> {
                    value.forEach {
                        FrontMatterListItem(it.toString())
                    }
                }

                is Boolean -> {
                    Checkbox(checked = value, modifier = Modifier.size(UI.propsRowHeight), onCheckedChange = {
                        updateFrontMatter(documentOf(key to it))
                    })
                }

                else -> Text(value.toString())
            }
        }
    }
}

@Composable
fun NoteTypography() = markdownTypography(
    h1 = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp, fontWeight = FontWeight.Black),
    h2 = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp, fontWeight = FontWeight.Black),
    h3 = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Black),
    h4 = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    h5 = MaterialTheme.typography.displayMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    h6 = MaterialTheme.typography.displayMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun PageTopBar(
    text: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(modifier.height(UI.tabHeight).fillMaxSize(), tonalElevation = UI.elevation.lv1 / 5f) {
        Box(Modifier.fillMaxSize().padding(UI.tabPadding), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                ProvideTextStyle(
                    MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.fade(
                            0.6f
                        )
                    )
                ) {
                    text()
                }
                Spacer(Modifier.weight(1f))
                buttons()
            }
        }
    }
}

@Composable
fun Note(
    path: VaultPath,
    vault: VaultViewModel = koinViewModel(),
) {
    val document by vault.observeDocument(path).collectAsState()
    val frontMatter = document?.frontMatter() ?: return
    val typography = NoteTypography()
    val title = path.displayName
//    val tasks: TasksViewModel = koinViewModel()
//    val propLoadable by tasks.getListProperties(key).collectAsState()
    var view: NoteView by remember { mutableStateOf(MarkdownView()) }

    AppScreen(text = { Text(path.pathString) }, topButtons = {
        if (view is EditView) IconButton(onClick = { view = MarkdownView() }) {
            Icon(Icons.AutoMirrored.Outlined.Notes, "List view")
        }
        if (view is MarkdownView) IconButton(onClick = { view = EditView() }) {
            Icon(AppIcons.EditNote, "Edit view")
        }
    }) {
//        NoteTopBar(path, currentView = view, onChangeView = { view = it })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier.sizeIn(maxWidth = UI.contentWidth).padding(UI.padding.md).fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(title, style = typography.h1)
                HorizontalDivider()
                NoteFrontMatter(frontMatter, updateFrontMatter = { vault.updateFrontMatter(path, it) })
                HorizontalDivider()
                Project(
                    path = path,
                    scrollable = false,
                )
                Spacer(Modifier.height(UI.padding.sm))
                document?.let { view.content(documentOf(), it) }
            }
        }
    }
}
