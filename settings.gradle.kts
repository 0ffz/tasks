pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "tasks"


include("targets:android", "targets:desktop", "app-common", "app-model", "tests", "database")
