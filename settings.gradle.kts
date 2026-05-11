rootProject.name = "tasks"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
//    includeBuild("../sqlite-kt")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    val catalogVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
//        maven("https://repo.mineinabyss.com/snapshots")
    }

    versionCatalogs {
        create("miaLibs") {
            from("com.mineinabyss:catalog:$catalogVersion")
        }
    }
}


include("app-client", "app-model", "app-server", "tests")
include("targets:desktop", "targets:android")
includeBuild("../syncengine")
//includeBuild("../sqlite-kt")
