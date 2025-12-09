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


include("app-android", "app-common", "app-desktop", "app-model", "app-server", "tests")

includeBuild("../syncengine")
includeBuild("../sqlite-kt")
