import androidx.compose.ui.window.application
import me.dvyy.tasks.app.data.createClientDatabase
import me.dvyy.tasks.app.ui.AppDesktop
import org.graalvm.nativeimage.ImageInfo
import org.graalvm.nativeimage.ProcessProperties
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

fun main() = application {
    if (ImageInfo.inImageRuntimeCode())
        System.setProperty("java.home", Path(ProcessProperties.getExecutableName()).parent.absolutePathString())
    createClientDatabase()
    AppDesktop()
}
