package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.VaultViewModel
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.layout.ui.LayoutStructure.Single
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Wrap
import me.dvyy.tasks.tasks.ui.elements.list.AllProjectsView
import org.dizitart.no2.collection.Document
import org.koin.compose.viewmodel.koinViewModel

object DpSerializer : KSerializer<Dp> {
    override val descriptor = Float.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Dp) = encoder.encodeFloat(value.value)
    override fun deserialize(decoder: Decoder) = Dp(decoder.decodeFloat())
}

@Serializable
sealed interface SplitAmount {
    @Serializable
    data class Fixed(val value: @Serializable(with = DpSerializer::class) Dp) : SplitAmount

    @Serializable
    data class Percent(val value: Float) : SplitAmount
}

@Serializable
sealed interface LayoutStructure {
    @Serializable
    data class Scrollable(
        val views: List<LayoutStructure>,
        val orientation: Orientation,
    ) : LayoutStructure

    @Serializable
    data class Split(
        val first: LayoutStructure,
        val second: LayoutStructure,
        val split: SplitAmount = SplitAmount.Percent(0.5f),
        val orientation: Orientation,
        val firstEnabled: Boolean = true,
        val secondEnabled: Boolean = true,
        val mergeWhenEmpty: Boolean = true,
    ) : LayoutStructure

    @Serializable
    sealed interface Single : LayoutStructure {
        val icon get() = Icons.Outlined.QuestionMark
        val text get() = "Untitled"

        enum class Location {
            Selected, TabList, Sidebar
        }

        @Composable
        fun DefaultTabLabel(
            icon: ImageVector?,
            text: String,
        ) = LeadingIcon(icon, text) {
            Text(
                text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        @Composable
        fun tabLabel(selected: Location) = DefaultTabLabel(icon, text)

        @Composable
        fun content()

        data class RichTextView(val file: String) : Single {
            override val icon get() = AppIcons.Description
            override val text get() = "Rich text"

            @Composable
            override fun content() {
            }
        }

        @Serializable
        data class WeekView(
            val startAtToday: Boolean = false,
            val takeDays: Int = 7,
        ) : Single {
            override val icon
                get() = when (takeDays) {
                    7 -> AppIcons.CalendarViewWeek
                    3 -> AppIcons.CalendarViewDay
                    else -> AppIcons.CalendarToday
                }

            override val text
                get() = when (takeDays) {
                    7 -> "Week view"
                    3 -> "3-day view"
                    else -> "Today"
                }

            @Composable
            override fun content() {
//                me.dvyy.tasks.tasks.ui.elements.list.WeekView(startAtToday = startAtToday, takeDays = takeDays)
            }
        }

        @Serializable
        data object FileTree : Single {
            override val icon get() = AppIcons.Folder
            override val text get() = "File tree"

            @Composable
            override fun content() {
                AppFileTree()
            }
        }

        abstract class Wrap(val wrap: Single) : Single by wrap {
        }

        @Serializable
        data class Projects(
            val staggered: Boolean = false,
            val horizontal: Boolean = false,
        ) : Single {
            override val icon
                get() = when {
                    horizontal -> AppIcons.HorizontalSplit
                    staggered -> AppIcons.Dashboard
                    else -> AppIcons.GridView
                }
            override val text
                get() = when {
                    horizontal -> "Horizontal"
                    staggered -> "Staggered"
                    else -> "Grid"
                }

            @Composable
            override fun content() {
                AllProjectsView(
                    horizontal = horizontal,
                    staggered = staggered,
                    modifier = if (staggered) Modifier.fillMaxHeight() else Modifier
                )
            }
        }

        @Serializable
        data class Project(
            val path: VaultPath,
        ) : Single {
            @Composable
            override fun tabLabel(location: Location) {
//                val tasks: TasksViewModel = koinViewModel()
//                val dialogs: DialogViewModel = koinViewModel()
//                val propsLoadable by tasks.getListProperties(key).collectAsState()
//                val props = propsLoadable.loadedOrNull() ?: run {
//                    Text("Loading project...")
//                    return
//                }
//                val icon = when {
//                    props.displayName?.contains(emojiRegex) == true -> null
//                    props.displayName == "Inbox" -> AppIcons.Inbox
//                    else -> AppIcons.Description
//                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DefaultTabLabel(icon, path.pathString.takeLastWhile { it != '/' })
                    if (location == Location.Sidebar) {
                        Spacer(Modifier.weight(1f))
//                        IconButton(
//                            onClick = { dialogs.show(AppDialog.ConfirmDeleteProject(key)) },
//                            modifier = Modifier.size(UI.size.md)
//                        ) {
//                            Icon(AppIcons.Close, "Delete project", tint = MaterialTheme.colorScheme.outline)
//                        }
                    }
                }
            }

            @OptIn(ExperimentalMaterial3ExpressiveApi::class)
            @Composable
            override fun content() {
                val vault = koinViewModel<VaultViewModel>()
                val document by vault.observeDocument(path).collectAsState()
                val frontMatter = document?.get("frontMatter") as? Document ?: return
                val typography = markdownTypography(
                    h1 = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp, fontWeight = FontWeight.Black),
                    h2 = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp, fontWeight = FontWeight.Black),
                    h3 = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Black),
                    h4 = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    h5 = MaterialTheme.typography.displayMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    h6 = MaterialTheme.typography.displayMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                )
//                val tasks: TasksViewModel = koinViewModel()
//                val propLoadable by tasks.getListProperties(key).collectAsState()
                val title = path.pathString.takeLastWhile { it != '/' }
                Column {
                    Surface(Modifier.height(UI.tabHeight).fillMaxSize().padding(UI.tabPadding)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Row {
                                Text(path.pathString.replace("/", " / "), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.fade(alpha = 0.6f))
                            }
                        }
                    }
//                    HorizontalDivider()
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Column(Modifier.sizeIn(maxWidth = 800.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                            Text(title, style = typography.h1)
                            frontMatter.forEach {
                                Row {
                                    Text(it.first)
                                    Text(it.second.toString())
                                }
                            }
                            document?.get("fileContent")?.let {
//                            val content = rememberRichTextState()
//                            LaunchedEffect(it) { content.setMarkdown(it.toString()) }
//                            RichText(content)
                                Markdown(it.toString(), typography = typography)
                            }
//                    Project(key, propLoadable, scrollable = false)
                        }
                    }
                }
            }

            companion object {
                val emojiRegex = Regex("^\\p{So}")
            }
        }
    }

    @Serializable
    data class Tabbed(
        val tabs: List<Single>,
        val selected: Int = 0,
        val name: String? = null,
        val fullWidth: Boolean = false,
        val selectable: Boolean = true,
    ) : LayoutStructure {
        fun withTab(
            tab: Single,
            select: Boolean = true,
            atIndex: Int = tabs.size,
        ): Tabbed {
            return Tabbed(tabs.toMutableList().apply {
                add(atIndex, tab)
            }, if (select) atIndex else selected)
        }
    }

//    @Serializable
//    data class Tab(val name: String, val content: LayoutStructure.Single)

    @Serializable
    data object Empty : Single {
        @Composable
        override fun content() {
        }
    }

    fun tabIfNecessary(tabName: String): Tabbed {
        return when (this) {
            is Tabbed -> this
            is Empty -> Tabbed(listOf(), 0)
            is Single -> Tabbed(listOf(this), 0)
            else -> error("Cannot convert $this to a tabbed layout")
        }
    }
}

inline fun Single.wrap(crossinline wrap: @Composable (original: @Composable () -> Unit) -> Unit): Single =
    object : Wrap(this) {
        @Composable
        override fun content() {
            wrap { super.content() }
        }
    }
