plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("android")
}


dependencies {
    implementation(project(":common"))
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
}

val androidKeystoreFile: String? by properties
val androidKeystorePassword: String? by properties
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
    signingConfigs {
        if (androidKeystoreFile != null) create("release") {
            properties["storeFile"]
            storeFile = file(androidKeystoreFile!!)
            storePassword = androidKeystorePassword
            keyAlias = "upload"
            keyPassword = androidKeystorePassword
        }
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
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // Includes a local, custom Proguard rules file
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
