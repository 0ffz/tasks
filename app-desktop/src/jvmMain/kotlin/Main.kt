import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1200.dp, height = 960.dp)
    ) {
        App(remember { createDatabase(DriverFactory()) })
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(remember { createDatabase(DriverFactory()) })
}
