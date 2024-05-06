plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin("android")
}


dependencies {
    implementation(project(":common"))
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
}

android {
    compileSdk = 34
    namespace = "me.dvyy"
    defaultConfig {
        applicationId = "me.dvyy"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
