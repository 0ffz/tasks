plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("android")
}


dependencies {
    implementation(project(":app-common"))
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
//    implementation(libs.androidx.glance.appwidget)
//    implementation(libs.androidx.glance.material3)
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")
    implementation(libs.kotlin.multiplatform.appdirs)
    implementation(libs.koin.android)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work.runtime.ktx)
}

val androidKeystoreFile: String? by project
val androidKeystorePassword: String? by project
android {
    buildFeatures {
        compose = true
    }
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
            if (androidKeystoreFile != null)
                signingConfig = signingConfigs.getByName("release")
        }
    }
}
