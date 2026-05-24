package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.TopBarContainer
import me.dvyy.tasks.layout.ui.layouts.TintedVerticalDivider
import me.dvyy.tasks.settings.ui.RowOrBox
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.helpers.optional


@Composable
fun AppScreens() {
    LocalUIState.current
//    Dialog(
//        onDismissRequest = { app.screen.update { null } },
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Box(Modifier.fillMaxSize().clickableWithoutRipple { app.screen.update { null } })
//        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            val padding = if (ui.isSmall) 0.dp else 32.dp
//            Surface(
//                Modifier
//                    .widthIn(max = 1280.dp)
//                    .optional(!ui.isSmall) { heightIn(max = 1200.dp) }
//                    .optional(ui.isSmall) { padding(top = UI.tabHeight * 0.75f) }
//                    .fillMaxSize().padding(padding),
//                shape = MaterialTheme.shapes.medium,
//                shadowElevation = 1.dp
//            ) {
//                Screens()
//            }
//        }
//    }
}

@Composable
private fun Screens(
) {
//    when (screen) {
//        is AppScreen.Settings -> SettingsScreen(screen.tab, onChangeTab = { app.showScreen(AppScreen.Settings(it)) })
//    }
}

@Composable
fun ScreenContainer(
    title: String,
    extraItems: @Composable RowScope.() -> Unit = {},
    utilityPane: (@Composable (setExpanded: (Boolean) -> Unit) -> Unit)? = null,
    utilityPaneText: String? = null,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val ui = UI
    var expanded by remember { mutableStateOf(true) }
    RowOrBox(!ui.isSmall) {
        AnimatedVisibility(
            !ui.isSmall || expanded,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {
            utilityPane?.let {
                Surface(shape = UI.shapes.rounded, tonalElevation = if (ui.isSmall) UI.elevation.lv0 else 0.5f.dp) {
                    Column(Modifier.optional(!ui.isSmall) { sizeIn(maxWidth = 300.dp) }.fillMaxHeight()) {
                        TopBarContainer {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                                Spacer(Modifier.weight(1f))
                                Text(utilityPaneText ?: "", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.weight(1f))
                                if (ui.isSmall) BoxButton(onClick = onClose) {
                                    Icon(Icons.Rounded.Close, "Close")
                                }
                            }
                        }
                        it({ expanded = it })
                    }
                }
            }
        }
        TintedVerticalDivider()
        AnimatedVisibility(
            !ui.isSmall || !expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                TopBarContainer {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (ui.isSmall) {
                            BoxButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Open menu")
                            }
                        }
                        extraItems()
                        Spacer(Modifier.weight(1f))
                        Text(title, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        BoxButton(onClick = onClose) {
                            Icon(Icons.Rounded.Close, "Close")
                        }
                    }
                }
                Column {
                    content()
                }
            }
        }
    }
}