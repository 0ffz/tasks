package me.dvyy.tasks.layout.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import co.touchlab.kermit.Logger
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.Folder
import dev.seyfarth.tablericons.outlined.LayoutCards
import dev.seyfarth.tablericons.outlined.Plus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.list.Project
import me.dvyy.tasks.tasks.ui.elements.list.rememberProjectDisplayOptions
import org.kodein.di.compose.viewmodel.rememberViewModel

object DpSerializer : KSerializer<Dp> {
    override val descriptor = Float.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Dp) = encoder.encodeFloat(value.value)
    override fun deserialize(decoder: Decoder) = Dp(decoder.decodeFloat())
}

@Serializable
@Immutable
sealed interface SplitAmount {
    @Serializable
    data class Fixed(val value: @Serializable(with = DpSerializer::class) Dp) : SplitAmount

    @Serializable
    data class Percent(val value: Float) : SplitAmount

    fun toDp(maxSize: Dp): Dp = when (this) {
        is Percent -> value * maxSize
        is Fixed -> value
    }.coerceIn(0.dp, maxSize)
}

class LayoutPath(val nodes: List<Node>) {
    enum class Node {
        FIRST, LAST
    }

    fun pop() = LayoutPath(nodes.drop(1))
}

@Serializable
@Immutable
private sealed interface LayoutStructure {
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
    data object Empty : Single() {
        override val text: String = "No tab open"
        override val showsTopBar: Boolean = false

        @Composable
        override fun content() {
        }
    }

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
//                AllProjectsScreen(
//                    horizontal = horizontal,
//                    projects = projects,
//                    staggered = staggered,
//                    modifier = if (staggered) Modifier.fillMaxHeight() else Modifier
//                )
            }
        }

        @Serializable
        data class Project(
            val key: ListId,
        ) : Single() {
            @Composable
            override fun trailingOptions() {
                BoxButton(onClick = {}) {
                    Icon(TablerIcons.Outlined.Plus, "Add to top")
                }
            }

            @Composable
            override fun tabLabel(location: Location) {
            }

            @Composable
            override fun content() {
                val tasksViewModel: TasksViewModel by rememberViewModel()
                val type = tasksViewModel.rememberUpdatedEntityType(key)
                when (type) {
                    "project" -> {
                        Project(key, modifier = Modifier, displayOptions = rememberProjectDisplayOptions(scrollable = true, fullHeight = true))
                    }

                    "layout" -> {
//                        val stored by remember(key) { tasksViewModel.watchLayout(key.uuid) }.collectAsState(initial = null)
//                        val layoutJson = stored?.layout ?: return
//                        val layout = runCatching { Json.decodeFromJsonElement(LayoutStructure.serializer(), layoutJson) }
//                            .getOrNull()
//                        if (layout == null) {
//                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                                Text("Error loading layout")
//                            }
//                        } else Layout(layout, onLayoutUpdate = { new ->
//                            tasksViewModel.updateLayout(key.uuid, new)
//                        })

                    }

                    else -> {}
                }
            }

            companion object {
                val emojiRegex = Regex("^\\p{So}")
            }
        }
    }

}
