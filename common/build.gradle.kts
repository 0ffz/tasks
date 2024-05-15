import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

composeCompiler {
    stabilityConfigurationFile = rootProject.file("compose_compiler_config.conf")
    enableStrongSkippingMode = true
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":model"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.compose.dnd)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
                implementation(libs.uuid)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization.json.eap)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.auth)
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
            }
        }
        val javaMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("ca.gosyer:kotlin-multiplatform-appdirs:1.2.0")
            }
        }
        val desktopMain by getting {
            dependsOn(javaMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        val androidMain by getting {
            dependsOn(javaMain)
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.activity.compose)
//                api("androidx.appcompat:appcompat:1.5.1")
//                api("androidx.core:core-ktx:1.9.0")
//                implementation(libs.coil.kt.compose)
//                implementation(libs.androidx.navigation.compose)
//                implementation(libs.androidx.compose.ui.util)
//                implementation(libs.androidx.lifecycle.viewModelCompose)
//                implementation(libs.androidx.constraintlayout.compose)
//                implementation(libs.androidx.core.ktx)
//                implementation(libs.androidx.activity.compose)
//                implementation(libs.androidx.lifecycle.viewModelCompose)
//                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }
    }
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
    kotlin {
        jvmToolchain(17)
    }
}
dependencies {
    implementation(libs.androidx.foundation.android)
}
