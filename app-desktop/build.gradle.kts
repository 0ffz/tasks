import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    id("de.undercouch.download") version "5.3.1"
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.slf4j)
                implementation(libs.koin.core)
                implementation(project(":app-common"))
                implementation(project(":app-model"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

// Conveyor
configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "jetsnak-mpp"
            packageVersion = "1.0.0"
        }
    }
}

// ==== Packaging ====

val appName = "Tasks"

val appInstallerName = "$appName-" + when {
    Os.isFamily(Os.FAMILY_MAC) -> "macOS"
    Os.isFamily(Os.FAMILY_WINDOWS) -> "windows"
    else -> "linux"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        buildTypes.release.proguard {
            configurationFiles.from(
                project.file("proguard/custom.pro")
            )
            optimize = true
            obfuscate = true
        }

        nativeDistributions {
            when {
                Os.isFamily(Os.FAMILY_MAC) -> targetFormats(TargetFormat.Dmg)
                Os.isFamily(Os.FAMILY_WINDOWS) -> targetFormats(TargetFormat.Exe)
                else -> targetFormats(TargetFormat.AppImage)
            }

            modules(
//                "java.instrument",
//                "java.management",
//                "java.naming",
                "java.sql",
//                "java.security.jgss",
//                "jdk.httpserver",
//                "jdk.unsupported"
            )
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
                dirChooser = false
                perUserInstall = false
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
        Os.isFamily(Os.FAMILY_MAC) -> "dmg"
        Os.isFamily(Os.FAMILY_WINDOWS) -> "exe"
        else -> "app"
    }
}"

tasks {
    val exeRelease by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from(composePackageDir)
        include("*.exe")
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
        src("https://github.com/AppImage/AppImageKit/releases/download/13/appimagetool-x86_64.AppImage")
        dest(appImageTool)
        doLast {
            exec {
                commandLine("chmod", "+x", appImageTool)
            }
        }
    }

    val deleteOldAppDirFiles by registering(Delete::class) {
        delete("$linuxAppDir/usr/bin", "$linuxAppDir/usr/lib")
    }

    val copyBuildToPackaging by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        dependsOn(deleteOldAppDirFiles)
        from("$buildDir/compose/binaries/main-release/app/$appName")
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
            Os.isFamily(Os.FAMILY_WINDOWS) -> dependsOn(exeRelease)
            Os.isFamily(Os.FAMILY_MAC) -> dependsOn(dmgRelease)
            else -> dependsOn(executeAppImageBuilder)
        }
    }
}

