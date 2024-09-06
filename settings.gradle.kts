pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        google()
//        mavenLocal()
        maven("https://maven.hq.hydraulic.software")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "tasks"


include("app-android", "app-common", "app-desktop", "app-web", "app-server", "app-model", "tests")
