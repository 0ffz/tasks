pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
    includeBuild("../sqlite-kt")
}

rootProject.name = "tasks"


include("app-client", "app-model", "app-server", "tests")
include("targets:desktop", "targets:android")
includeBuild("../syncengine")
includeBuild("../sqlite-kt")
