pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        google()
        maven("https://maven.hq.hydraulic.software")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
    }

    versionCatalogs {
        create("idofrontLibs") {
            from("com.mineinabyss:catalog:$idofrontVersion")
            version("compose", "1.6.2")
        }
    }
}

rootProject.name = "tasks"
