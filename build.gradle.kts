import nl.littlerobots.vcu.plugin.versionSelector

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(miaLibs.plugins.jetbrainsCompose) apply false
    alias(miaLibs.plugins.kotlin.jvm) apply false
    alias(miaLibs.plugins.kotlin.multiplatform) apply false
    alias(miaLibs.plugins.compose.compiler) apply false
    alias(miaLibs.plugins.compose.hot.reload) apply false
    alias(miaLibs.plugins.jib) apply false
    alias(miaLibs.plugins.version.catalog.update)
    alias(miaLibs.plugins.gradle.versions)
    alias(miaLibs.plugins.sqlite.kt.codegen) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
//        mavenLocal()
    }
}

tasks {
    updateDaemonJvm {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.JETBRAINS
    }
    versionCatalogUpdate {
        keep {
            keepUnusedVersions = true
        }
        versionSelector { !isNonStable(it.candidate.version) || isNonStable(it.currentVersion) }
    }
}

fun isNonStable(version: String): Boolean {
    val unstableKeywords = listOf("-beta", "-dev", "+dev", "x-", "-rc", "-alpha", "-SNAPSHOT")
    return unstableKeywords.any { version.contains(it, ignoreCase = true) }
}
