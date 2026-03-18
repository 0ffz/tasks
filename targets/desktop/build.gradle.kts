import de.undercouch.gradle.tasks.download.Download
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeHotReload)
    id("de.undercouch.download") version "5.3.1"
//    id("org.graalvm.buildtools.native") version "0.10.4"
}


tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("MainKt")
}
kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.slf4j)
    implementation(libs.koin.core)
    implementation(project(":app-client"))
    implementation(project(":app-model"))
    implementation(compose.desktop.currentOs)
    implementation(libs.graalvm.library.support)

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
        mainClass = "MainKt"
        buildTypes.release.proguard {
            isEnabled = false
        }
        nativeDistributions {
            when {
                os.isMacOsX -> targetFormats(TargetFormat.Dmg)
                os.isWindows -> targetFormats(TargetFormat.Msi)
                else -> targetFormats(TargetFormat.AppImage)
            }

            modules("java.sql")
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
val appImageTool = project.file("packaging/deps/appimagetool.AppImage")
val composePackageDir = "$buildDir/compose/binaries/main-release/${
    when {
        os.isMacOsX -> "dmg"
        os.isWindows -> "msi"
        else -> "app"
    }
}"

tasks {
    register("runFix") {
        dependsOn("run")
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
        onlyIf { !appImageTool.exists() }
        src("https://github.com/AppImage/appimagetool/releases/download/1.9.1/appimagetool-x86_64.AppImage")
        dest(appImageTool)
        doLast {
            providers.exec { commandLine("chmod", "+x", appImageTool) }
        }
    }

    val deleteOldAppDirFiles by registering(Delete::class) {
        delete("$linuxAppDir/usr/bin", "$linuxAppDir/usr/lib")
    }

    val copyBuildToPackaging by registering(Copy::class) {
//        dependsOn(nativeCompile)
        dependsOn(deleteOldAppDirFiles)
        from("build/native/nativeCompile/")
        into("$linuxAppDir/usr")
    }

    val executeAppImageBuilder by registering(Exec::class) {
        dependsOn(downloadAppImageBuilder)
        dependsOn(copyBuildToPackaging)
        environment("ARCH", "x86_64")
        commandLine(
            appImageTool,
            linuxAppDir.absolutePath,
            project.file("releases/$appInstallerName-${project.version}.AppImage")
        )
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
//            mainClass.set("MainKt")
//            imageName.set("tasks")
//            buildArgs(
//                "-O2",
//                "-Djava.awt.headless=false",
//                "--strict-image-heap", // kotlin 2.0 fix
//                "-H:+ReportExceptionStackTraces",
//                "-R:MaxHeapSize=300M",
//                "-H:+AddAllCharsets",
//            )
//
//            // Don't open terminal when running exe on Windows
//            if (os.isWindows) buildArgs.addAll(
//                "-H:NativeLinkerOption=/SUBSYSTEM:WINDOWS",
//                "-H:NativeLinkerOption=/ENTRY:mainCRTStartup",
//            )
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
