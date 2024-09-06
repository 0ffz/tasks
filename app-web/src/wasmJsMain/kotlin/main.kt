import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import app.cash.sqldelight.async.coroutines.awaitCreate
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createClientDatabase
import me.dvyy.tasks.app.ui.AppWeb
import me.dvyy.tasks.app.ui.createAppKoinApplication
import me.dvyy.tasks.db.client.Database
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.w3c.dom.Window

external val window: Window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {
    val driver = DriverFactory().createDriver()
    Database.Schema.awaitCreate(driver)
    startKoin(createAppKoinApplication {
        modules(
            module {
                single {
                    createClientDatabase(driver)
                }
            }
        )
    })

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        AppWeb()
    }
}
