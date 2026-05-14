package me.dvyy.tasks

import androidx.compose.ui.window.application
import me.dvyy.tasks.app.ui.AppDesktop

fun main() = application {
//    if (ImageInfo.inImageRuntimeCode())
//        System.setProperty("java.home", Path(ProcessProperties.getExecutableName()).parent.absolutePathString())
    AppDesktop()
}
