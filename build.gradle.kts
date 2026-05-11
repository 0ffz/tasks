import nl.littlerobots.vcu.plugin.versionSelector

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.jib) apply false
    alias(miaLibs.plugins.version.catalog.update)
    alias(miaLibs.plugins.gradle.versions)
    id("me.dvyy.sqlite.codegen") version "0.0.3-alpha.2" apply false
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
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
