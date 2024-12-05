package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.dialogs.AppDialog
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.layout.ui.LayoutStructure.Single
import me.dvyy.tasks.layout.ui.LayoutStructure.Single.Wrap
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.AllProjectsView
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.utils.loadedOrNull
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
                me.dvyy.tasks.tasks.ui.elements.list.WeekView(startAtToday = startAtToday, takeDays = takeDays)
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

        abstract class Wrap(val wrap: Single): Single by wrap {
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
            val key: ListId,
        ) : Single {
            @Composable
            override fun tabLabel(location: Location) {
                val tasks: TasksViewModel = koinViewModel()
                val dialogs: DialogViewModel = koinViewModel()
                val propsLoadable by tasks.getListProperties(key).collectAsState()
                val props = propsLoadable.loadedOrNull() ?: run {
                    Text("Loading project...")
                    return
                }
                val icon = when {
                    props.displayName?.contains(emojiRegex) == true -> null
                    props.displayName == "Inbox" -> AppIcons.Inbox
                    else -> AppIcons.Description
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DefaultTabLabel(icon, props.displayName ?: "Untitled")
                    if (location == Location.Sidebar) {
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { dialogs.show(AppDialog.ConfirmDeleteProject(key)) },
                            modifier = Modifier.size(UI.size.md)
                        ) {
                            Icon(AppIcons.Close, "Delete project", tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            @Composable
            override fun content() {
                val tasks: TasksViewModel = koinViewModel()
                val propLoadable by tasks.getListProperties(key).collectAsState()
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Project(key, propLoadable, scrollable = false)
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

inline fun Single.wrap(crossinline wrap: @Composable (original: @Composable () -> Unit) -> Unit): Single = object : Wrap(this) {
    @Composable
    override fun content() {
        wrap { super.content() }
    }
}
