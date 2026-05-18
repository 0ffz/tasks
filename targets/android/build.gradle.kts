import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(miaLibs.plugins.jetbrainsCompose)
    alias(miaLibs.plugins.compose.compiler)
}


kotlin {
    jvmToolchain(17)
}
dependencies {
    implementation(project(":app-client"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.kotlin.multiplatform.appdirs)
    implementation(miaLibs.koin.android)
//    implementation(libs.koin.androidx.workmanager)
    implementation(miaLibs.kermit)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work.runtime.ktx)
}

val keystoreProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}
val androidKeystoreFile: String? by keystoreProperties
val androidKeystorePassword: String? by keystoreProperties

base {
    archivesName.set("Tasks-$version")
}
android {
    buildFeatures {
        compose = true
    }
    compileSdk = 36
    namespace = "me.dvyy.tasks"
    defaultConfig {
        applicationId = "me.dvyy.tasks"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = version.toString()
    }

    signingConfigs {
        if (androidKeystoreFile != null) register("release") {
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
    buildTypes {
        debug {
//            applicationIdSuffix = ".debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

//            applicationVariants.all {
//                outputs.configureEach {
//                    outputFile = outputFile.
//                }
//            }

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
