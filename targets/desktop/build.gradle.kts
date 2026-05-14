import de.undercouch.gradle.tasks.download.Download
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun

plugins {
    alias(miaLibs.plugins.kotlin.jvm)
    alias(miaLibs.plugins.jetbrainsCompose)
    alias(miaLibs.plugins.compose.compiler)
    alias(miaLibs.plugins.compose.hot.reload)
    id("de.undercouch.download") version "5.3.1"
    alias(miaLibs.plugins.shadowjar)
//    alias(miaLibs.plugins.graalvm.nativeimage)
}


tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("me.dvyy.tasks.MainKt")
}

dependencies {
    implementation(libs.slf4j)
    implementation(miaLibs.koin.core)
    implementation(project(":app-client"))
    implementation(project(":app-model"))
//    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.macos_arm64)
    implementation(compose.desktop.windows_x64)
    implementation(compose.desktop.linux_x64)
//    implementation(libs.graalvm.library.support)

}

// ==== Packaging ====

val appName = "Tasks"

val os: OperatingSystem = OperatingSystem.current()

val appInstallerName = "$appName-" + when {
    os.isMacOsX -> "macOS"
    os.isWindows -> "windows"
    else -> "linux"
}

compose.desktop {
    application {
        mainJar = tasks.shadowJar.get().archiveFile
        mainClass = "me.dvyy.tasks.MainKt"
//        "-Dawt.toolkit.name=WLToolkit",
        jvmArgs.addAll(listOf("--enable-native-access=ALL-UNNAMED"))
        buildTypes.release.proguard {
            isEnabled = true
            optimize = true
            joinOutputJars = true
            configurationFiles.from(project.file("proguard/custom.pro"))
        }
        nativeDistributions {
            when {
                os.isMacOsX -> targetFormats(TargetFormat.Dmg)
                os.isWindows -> targetFormats(TargetFormat.Msi)
                else -> targetFormats(TargetFormat.AppImage)
            }

            packageName = appName
            packageVersion = "${project.version}"
            val strippedVersion = project.version.toString().substringBeforeLast("-")
            val iconsRoot = project.file("packaging/icons")
            macOS {
                packageVersion = strippedVersion
                iconFile.set(iconsRoot.resolve("icon.icns"))
            }
            windows {
                packageVersion = strippedVersion
                menu = true
                shortcut = true
                upgradeUuid = "ac99e6ed-7dbf-410b-bd3b-e9a143cebcd7"
                iconFile.set(iconsRoot.resolve("icon.ico"))
                dirChooser = true
                perUserInstall = true
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}

val linuxAppDir = project.file("packaging/appimage/$appName.AppDir")
val composePackageDir = "$buildDir/compose/binaries/main-release/${
    when {
        os.isMacOsX -> "dmg"
        os.isWindows -> "msi"
        else -> "app"
    }
}"

interface InjectedExecOps {
    @get:Inject
    val execOps: ExecOperations
}

tasks {
    shadowJar {
        archiveBaseName = "tasks"
        minimize {
//            exclude { it.moduleGroup == "io.ktor" }
//            exclude { it.moduleGroup == "androidx.compose.runtime" }
//            exclude { it.moduleGroup == "org.jetbrains.skiko" }
//            exclude(dependency(libs.kotlinx.coroutines.swing))
            include { it.moduleGroup == "androidx.compose.material" }
        }
        manifest {
            attributes("Main-Class" to "MainKt")
        }
    }
    val windowsRelease by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from(composePackageDir)
        include("*.msi")
        rename("$appName*", appInstallerName)
        into("releases")
    }

    val dmgRelease by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from(composePackageDir)
        include("*.dmg")
        rename("$appName*", appInstallerName)
        into("releases")
    }

    // Appimage
    val downloadAppImageBuilder by registering(Download::class) {
        val appImageTool = layout.projectDirectory.file("packaging/deps/appimagetool.AppImage")
        outputs.file(appImageTool)
        onlyIf { !appImageTool.asFile.exists() }
        src("https://github.com/AppImage/appimagetool/releases/download/1.9.1/appimagetool-x86_64.AppImage")
        dest(appImageTool)
        val injected = project.objects.newInstance<InjectedExecOps>()
        doLast {
            injected.execOps.exec {
                commandLine("chmod", "+x", appImageTool.asFile.absolutePath)
            }
        }
    }

    val copyBuildToPackaging by registering(Sync::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from("build/compose/binaries/main-release/app/Tasks")
        into(linuxAppDir.resolve("usr"))
        filePermissions {
            user { read = true; write = true; execute = true }
        }
    }

    val executeAppImageBuilder by registering(Exec::class) {
        val appImageTool = project.file("packaging/deps/appimagetool.AppImage")
        val outputFile = project.file("releases/$appInstallerName-${project.version}.AppImage")
//        outputs.file(outputFile)
        dependsOn(downloadAppImageBuilder)
        dependsOn(copyBuildToPackaging)
        environment("ARCH", "x86_64")
        commandLine(appImageTool, linuxAppDir.absolutePath, outputFile)
    }


    val packageForRelease by registering {
        mkdir(project.file("releases"))
        when {
            os.isMacOsX -> dependsOn(dmgRelease)
            os.isWindows -> dependsOn(windowsRelease)
            else -> dependsOn(executeAppImageBuilder)
        }
    }
}

//graalvmNative {
//    toolchainDetection.set(false)
//    binaries {
//        named("main") {
//            mainClass.set("me.dvyy.tasks.MainKt")
//            imageName.set("tasks")
//            buildArgs(
//                "-O2",
//                "-Djava.awt.headless=false",
////                "--strict-image-heap", // kotlin 2.0 fix
////                "-H:+ReportExceptionStackTraces",
////                "-R:MaxHeapSize=300M",
////                "-H:+AddAllCharsets",
//            )
////
////            // Don't open terminal when running exe on Windows
////            if (os.isWindows) buildArgs.addAll(
////                "-H:NativeLinkerOption=/SUBSYSTEM:WINDOWS",
////                "-H:NativeLinkerOption=/ENTRY:mainCRTStartup",
////            )
//            configurationFileDirectories.from("native-image/${os.familyName}")
//        }
//    }
//
//    agent {
//        defaultMode.set("standard")
//
//        metadataCopy {
//            inputTaskNames.add("run") // Tasks previously executed with the agent attached.
//            outputDirectories.add("native-image/${os.familyName}")
//            mergeWithExisting.set(true)
//        }
//    }
//}

tasks {
    val copyLibjawt = register<ProcessResources>("copyLibjawt") {
        val source = when {
            os.isWindows -> "build/compose/binaries/main/app/$appName/runtime/bin/jawt.dll"
            os.isUnix -> "build/compose/binaries/main/app/$appName/lib/runtime/lib/libjawt.so"
            else -> return@register
        }
        val target = when {
            os.isWindows -> "build/native/nativeCompile/bin"
            os.isUnix -> "build/native/nativeCompile/lib"
            else -> return@register
        }
        dependsOn("createDistributable")
        from(source)
        into(target)
    }

//    nativeCompile {
//        dependsOn(copyLibjawt)
//    }
}
