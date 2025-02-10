plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()
    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":app-model"))
                implementation(project(":database"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.preview)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
//                implementation(libs.uuid)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.auth)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.serialization)
                implementation(compose.components.resources)
                implementation(libs.material3.window.sizeclass.multiplatform)
                implementation(libs.navigation.compose)
//                implementation(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlin.result)
                implementation("org.kodein.emoji:emoji-kt:2.0.1")
                implementation("org.kodein.emoji:emoji-compose-m3:2.0.1")
//                implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc10"))
                implementation("com.mikepenz:multiplatform-markdown-renderer:0.30.0")
                implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.30.0")
                implementation("com.mikepenz:multiplatform-markdown-renderer-code:0.30.0")

                implementation(libs.nitrite.core)
                implementation("io.github.theapache64:rebugger:1.0.0-rc03")
                implementation("com.github.alorma.compose-settings:ui-tiles:2.10.0")
                implementation("com.github.alorma.compose-settings:ui-tiles-extended:2.10.0")
            }
        }
        val jvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kotlin.multiplatform.appdirs)
            }
        }
        val desktopMain by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.cio)
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.ui)
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
            }
        }

//        val wasmJsMain by getting {
//            dependencies {
//                //TODO waiting for wasmJs driver
//                implementation(libs.sqldelight.web.worker.driver.wasm)
//                implementation(devNpm("copy-webpack-plugin", "9.1.0"))
//            }
//        }
    }
}

composeCompiler {
    stabilityConfigurationFile = rootProject.file("compose_compiler_config.conf")
}

android {
    namespace = "me.dvyy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
