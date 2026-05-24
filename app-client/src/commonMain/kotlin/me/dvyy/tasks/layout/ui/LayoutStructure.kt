package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Calendar
import dev.seyfarth.tablericons.outlined.CalendarMonth
import dev.seyfarth.tablericons.outlined.CalendarWeek
import dev.seyfarth.tablericons.outlined.FileDescription
import dev.seyfarth.tablericons.outlined.Folder
import dev.seyfarth.tablericons.outlined.LayoutCards
import dev.seyfarth.tablericons.outlined.X
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.elements.WeekViewActions
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.layout.ui.layouts.Layout
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import me.dvyy.tasks.tasks.ui.elements.views.AllProjectsView
import me.dvyy.tasks.tasks.ui.elements.views.WeekView
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

class LayoutPath(val nodes: List<Node>) {
    enum class Node {
        FIRST, LAST
    }

    fun pop() = LayoutPath(nodes.drop(1))
}
@Serializable
sealed interface LayoutStructure {
    /**
     * Returns a new structure, without the leaf structure at this [path]
     */
//    operator fun minus(path: LayoutPath): LayoutStructure {
//
//    }
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


    class Wrap(
        val wrap: @Composable (original: @Composable () -> Unit) -> Unit,
        val child: LayoutStructure,
    ) : LayoutStructure

    @Serializable
    sealed class Single : LayoutStructure {
        open val icon: ImageVector get() = Icons.Outlined.QuestionMark
        open val text: String get() = "Untitled"
        open val hasDropTargets get() = true
        open val showsTopBar get() = true

        @Transient
        private val content: @Composable () -> Unit = movableContentOf { content() }

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
        open fun tabLabel(selected: Location) = DefaultTabLabel(icon, text)

        @Composable
        open fun trailingOptions() {
        }

        @Composable
        fun cachedContent() {
            Logger.v { "$text had content $content" }
            content.invoke()
        }

        @Composable
        abstract fun content()

        data class RichTextView(val file: String) : Single() {
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
        ) : Single() {
            override val icon
                get() = when (takeDays) {
                    7 -> TablerIcons.Outlined.CalendarWeek
                    3 -> TablerIcons.Outlined.CalendarMonth
                    else -> TablerIcons.Outlined.Calendar
                }

            override val text
                get() = when (takeDays) {
                    7 -> "Week view"
                    3 -> "3-day view"
                    else -> "Today"
                }

            @Composable
            override fun trailingOptions() {
                WeekViewActions()
            }

            @Composable
            override fun content() {
                BoxWithConstraints {
                    WeekView(
                        startAtToday = startAtToday, takeDays = takeDays, isSmall = maxWidth < 600.dp
                    )
                }
            }

        }

        @Serializable
        data object FileTree : Single() {
            override val icon = TablerIcons.Outlined.Folder
            override val text = "File tree"
            override val hasDropTargets: Boolean = false
            override val showsTopBar: Boolean = false

            @Composable
            override fun content() {
                AppFileTree()
            }
        }

        @Serializable
        data class Projects(
            val staggered: Boolean = false,
            val horizontal: Boolean = false,
            val projects: List<ListId>? = null,
        ) : Single() {
            override val icon get() = TablerIcons.Outlined.LayoutCards
            override val text get() = "All Projects"

            @Composable
            override fun content() {
                AllProjectsView(
                    horizontal = horizontal,
                    projects = projects,
                    staggered = staggered,
                    modifier = if (staggered) Modifier.fillMaxHeight() else Modifier
                )
            }
        }

        @Serializable
        data class Project(
            val key: ListId,
        ) : Single() {
            @Composable
            override fun tabLabel(location: Location) {
                val tasks: TasksViewModel = koinViewModel()
                val title = tasks.watchProjectTitle(key.uuid).collectAsState(initial = null).value?.title
                val icon = when {
                    //TODO reimplement
//                    props.displayName?.contains(emojiRegex) == true -> null
//                    props.displayName == "Inbox" -> AppIcons.Inbox
                    else -> TablerIcons.Outlined.FileDescription
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        DefaultTabLabel(icon, title ?: "Untitled")
                    }
                    if (location == Location.Sidebar) {
                        BoxButton(
                            onClick = { TODO("Open project remove dialog") /*dialogs.show(AppDialog.ConfirmDeleteProject(key))*/ },
                        ) {
                            Icon(TablerIcons.Outlined.X, "Delete project", tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            @Composable
            override fun content() {
                val tasksViewModel = koinViewModel<TasksViewModel>()
                val type = tasksViewModel.rememberUpdatedEntityType(key)
                when (type) {
                    "project" -> {
                        Project(key, displayOptions = rememberProjectDisplayOptions(scrollable = true, fullHeight = true))
                    }

                    "layout" -> {
                        val stored by remember(key) { tasksViewModel.watchLayout(key.uuid) }.collectAsState(initial = null)
                        val layoutJson = stored?.layout ?: return
                        val layout = runCatching { Json.decodeFromJsonElement(LayoutStructure.serializer(), layoutJson) }
                            .getOrNull()
                        if (layout == null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error loading layout")
                            }
                        } else Layout(layout, onLayoutUpdate = { new ->
                            tasksViewModel.updateLayout(key.uuid, new)
                        })

                    }

                    else -> {}
                }
            }

            companion object {
                val emojiRegex = Regex("^\\p{So}")
            }
        }
    }

    @Serializable
    data class Tabbed(
        val tabs: List<LayoutStructure>,
        val selected: Int = 0,
        val name: String? = null,
        val fullWidth: Boolean = false,
        val selectable: Boolean = true,
    ) : LayoutStructure {
        fun withTab(
            tab: LayoutStructure,
            select: Boolean = true,
            atIndex: Int = tabs.size,
            replace: Boolean = false,
        ): Tabbed {
            if (tab == Remove) return Tabbed(tabs.filterIndexed { index, _ -> index != atIndex })
            if (atIndex == tabs.size && tabs.lastOrNull() == Empty) return Tabbed(
                tabs.dropLast(1) + tab,
                if (select) tabs.lastIndex else selected
            )
            return Tabbed(tabs.toMutableList().apply {
                if (replace) removeAt(atIndex)
                add(atIndex, tab)
            }, if (select) atIndex else selected)
        }
    }

//    @Serializable
//    data class Tab(val name: String, val content: LayoutStructure.Single)

    @Serializable
    data object Empty : Single() {
        override val text: String = "No tab open"
        override val showsTopBar: Boolean = false

        @Composable
        override fun content() {
        }
    }

    @Serializable
    data object Remove : Single() {
        @Composable
        override fun content() {
        }
    }

    fun tabIfNecessary(tabName: String): Tabbed {
        return when (this) {
            is Tabbed -> this
            is Remove -> Tabbed(listOf(), 0)
            is Single -> Tabbed(listOf(this), 0)
            else -> error("Cannot convert $this to a tabbed layout")
        }
    }
}

fun LayoutStructure.wrap(
    wrap: @Composable (original: @Composable () -> Unit) -> Unit,
): LayoutStructure.Wrap {
    return LayoutStructure.Wrap(wrap, this)
}
