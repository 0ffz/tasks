pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        google()
        maven("https://maven.hq.hydraulic.software")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        mavenLocal()
    }
}

rootProject.name = "tasks"


include("android", "common", "desktop", "web", "server", "model")
