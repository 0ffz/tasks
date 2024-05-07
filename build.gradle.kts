plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

//kotlin {
////    androidTarget {
////        compilations.all {
////            kotlinOptions {
////                jvmTarget = "1.8"
////            }
////        }
////    }
//    applyDefaultHierarchyTemplate()
//
//
//    jvm("desktop")
//
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(project.projectDir.path)
//                        add(project.projectDir.path + "/commonMain/")
//                        add(project.projectDir.path + "/wasmJsMain/")
//                    }
//                }
//            }
//        }
//        binaries.executable()
//    }
//
//    sourceSets {
//        val desktopMain by getting
//
////        androidMain.dependencies {
////            implementation(libs.compose.ui.tooling.preview)
////            implementation(libs.androidx.activity.compose)
////        }
//        desktopMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//        commonMain.dependencies {
//        }
//    }
//}
//
////android {
////    namespace = "org.example.project"
////    compileSdk = libs.versions.android.compileSdk.get().toInt()
////
////    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
////    sourceSets["main"].res.srcDirs("src/androidMain/res")
////    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
////
////    defaultConfig {
////        applicationId = "org.example.project"
////        minSdk = libs.versions.android.minSdk.get().toInt()
////        targetSdk = libs.versions.android.targetSdk.get().toInt()
////        versionCode = 1
////        versionName = "1.0"
////    }
////    packaging {
////        resources {
////            excludes += "/META-INF/{AL2.0,LGPL2.1}"
////        }
////    }
////    buildTypes {
////        getByName("release") {
////            isMinifyEnabled = false
////        }
////    }
////    compileOptions {
////        sourceCompatibility = JavaVersion.VERSION_1_8
////        targetCompatibility = JavaVersion.VERSION_1_8
////    }
////    dependencies {
////        debugImplementation(libs.compose.ui.tooling)
////    }
////}
//
//compose.desktop {
//    application {
//        jvmArgs += listOf("-Xss500K")
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "org.example.project"
//            packageVersion = "1.0.0"
//        }
//    }
//}
//
//compose.experimental {
//    web.application {}
//}
