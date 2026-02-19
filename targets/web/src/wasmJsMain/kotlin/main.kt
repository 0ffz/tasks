import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import me.dvyy.tasks.app.data.DatabaseWrapper
import me.dvyy.tasks.app.ui.AppWeb
import me.dvyy.tasks.app.ui.createAppKoinApplication
import me.dvyy.tasks.db.client.Database
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.w3c.dom.Window

external val window: Window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {
    val database = DatabaseWrapper.create().initializeDatabase()
    startKoin(createAppKoinApplication {
        modules(
            module {
                single<Database> { database }
            }
        )
    })

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        AppWeb()
    }
}
