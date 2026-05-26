package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.X
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.components.LeadingIcon
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.layout.ui.SplitAmount
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.ButtonRow
import kotlin.uuid.Uuid

@Immutable
sealed interface LayoutOperation {
    val id: Uuid

    data class Split(
        val amount: SplitAmount = SplitAmount.Percent(0.5f),
        val orientation: Orientation = Orientation.Horizontal,
        override val id: Uuid = Uuid.random(),
    ) : LayoutOperation

    data class Place(
        val destination: ScreenDest,
        override val id: Uuid = Uuid.random(),
    ) : LayoutOperation
}

@Immutable
class LayoutDefinition private constructor(
    val operations: PersistentList<LayoutOperation> = persistentListOf(),
) {
    fun after(element: Int): Int {
        var count = 1
        var i = element
        while (i < operations.size) {
            if (operations[i] is LayoutOperation.Split) count += 2
            else count--
            i++
            if (count == 0) break
        }
        return i
    }

    fun mutate(block: (MutableList<LayoutOperation>) -> Unit): LayoutDefinition {
        val mutated = operations.mutate(block)
        return if (mutated.isEmpty()) Empty
        else LayoutDefinition(mutated)
    }

    fun elementRange(index: Int): IntRange {
//        if (index !in operations.indices) return index..<index
//        if (operations[index] is LayoutOperation.Split) return index..<after(index)
//        var count = 1
//        var i = index
//        while (i < operations.size) {
//            if (operations[i] is LayoutOperation.Split) count++
//            else count--
//            i++
//            if (count == 0) break
//        }
        return index..<after(index)
    }

    fun parent(element: Int): Int {
        if (operations[element] is LayoutOperation.Split) return element - 1
        var count = 1
        var i = element - 1
        while (i >= 0) {
            if (operations[i] is LayoutOperation.Split) count -= 2
            else count++
            if (count <= 0) break
            i--
        }
        return i
    }

    fun <T> MutableList<T>.removeAll(range: IntRange) = repeat(range.last - range.first + 1) { removeAt(range.first) }

    fun minus(index: Int): LayoutDefinition {
        if (index !in operations.indices) return this
        return mutate { list ->
            list.removeAll(elementRange(index))
            if (operations[index] !is LayoutOperation.Split) {
                val parent = parent(index)
                if (parent != -1) list.removeAt(parent)
            }
        }
    }

    fun indexOf(id: Uuid?) = if (id == null) -1 else operations.indexOfFirst { it.id == id }

    fun minus(id: Uuid?) = minus(indexOf(id))

    fun replace(index: Int, dest: ScreenDest): LayoutDefinition {
        if (index !in operations.indices) return this
        return mutate { list ->
            list[index] = LayoutOperation.Place(dest)
        }
    }

    fun replace(index: Int, split: LayoutOperation.Split, first: ScreenDest, second: ScreenDest): LayoutDefinition {
        return mutate { list ->
            val next = after(index)
            repeat(next - index) {
                list.removeAt(index)
            }
            list.add(index, split)
            list.add(index + 1, LayoutOperation.Place(first))
            list.add(index + 2, LayoutOperation.Place(second))
        }
    }

    val placeOperations get() = operations.filterIsInstance<LayoutOperation.Place>()

    companion object {
        fun of(destination: ScreenDest) = LayoutDefinition(persistentListOf(LayoutOperation.Place(destination)))
        val Empty = of(ScreenDest.Empty)
    }

}

@Composable
fun ScreenTab(
    screen: ScreenDest,
    modifier: Modifier = Modifier,
) {
    val screen = screen.toScreen()
    LeadingIcon({ Icon(screen.icon, "Icon") }) {
        screen.tabLabel()
    }
}

@Composable
fun LayoutTab(
    selected: Boolean,
    definition: LayoutDefinition,
    modifier: Modifier = Modifier,
    trailingOptions: @Composable () -> Unit = {},
) = Surface(modifier, tonalElevation = UI.elevation.lv1) {
    ButtonRow(Modifier.height(UI.tabHeight)) {
        Row(Modifier.weight(1f)) {
            definition.placeOperations.forEach {
                val screen = it.destination
                ScreenTab(screen)
            }
        }
        trailingOptions()
    }
}

// TODO merge with ScreenTab
@Composable
private fun TabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickableWithoutRipple(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                tonalElevation = UI.elevation.lv1,
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonRow {
                    Spacer(Modifier.width(UI.padding.sm))
                    Row(Modifier.weight(1f)) {
                        title()
                    }
                    BoxButton(onClick = { onClose() }) {
                        Icon(TablerIcons.Outlined.X, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )

            Surface {
                Box(Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}