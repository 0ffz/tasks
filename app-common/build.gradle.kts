import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
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
                implementation(project(":app-model"))
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
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.serialization)
                implementation(compose.components.resources)
                implementation(libs.material3.window.sizeclass.multiplatform)
                implementation(libs.navigation.compose)
//                implementation(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.result)
                implementation(libs.primitive.adapters)
                implementation(libs.coroutines.extensions)
                implementation("org.kodein.emoji:emoji-kt:2.0.1")
                implementation("org.kodein.emoji:emoji-compose-m3:2.0.1")
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
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.activity.compose)
                implementation(libs.sqldelight.android.driver)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                //TODO waiting for wasmJs driver
//                implementation(libs.sqldelight.web.worker.driver)
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("me.dvyy.tasks.db")
//            sourceFolders = listOf("sqldelight")
//            dialect("app.cash.sqldelight:mysql-dialect:2.0.2")
        }
    }
}
