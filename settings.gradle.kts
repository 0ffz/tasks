pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "tasks"


include("app-android", "app-common", "app-desktop", "app-model")

includeBuild("../syncengine")