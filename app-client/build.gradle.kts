plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
//    alias(libs.plugins.stability.analyzer)
}
composeCompiler {
    stabilityConfigurationFiles.addAll(project.layout.projectDirectory.file("compose_compiler_config.conf"))
}

compose {
    resources {
        generateResClass = always
    }
}

kotlin {
    androidLibrary {
        namespace = "me.dvyy.tasks.dev"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
//        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
        androidResources {
            enable = true
        }
    }
    applyDefaultHierarchyTemplate()
    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xcontext-parameters")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }

    jvm("desktop")

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser {
//            commonWebpackConfig {
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(project.projectDir.path)
//                    }
//                }
//            }
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":app-model"))
                implementation("org.jetbrains.compose.runtime:runtime:1.10.0-rc02")
                implementation("org.jetbrains.compose.foundation:foundation:1.10.0-rc02")
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0-rc02")
                implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha05")
//                implementation("org.jetbrains.compose.material3:material3:1.9.0-beta03")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation("org.jetbrains.compose.ui:ui:1.10.0-rc02")
                implementation("io.github.theapache64:rebugger:1.0.1")
//                implementation("app.cash.molecule:molecule-runtime:2.2.0")
//                implementation("com.mohamedrejeb.dnd:compose-dnd:0.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
//                implementation(libs.uuid)

                implementation(libs.filekit.dialogs)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.serialization.protobuf)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.auth)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.serialization)
                implementation(libs.components.resources)
                implementation(libs.material3.window.sizeclass.multiplatform)
                implementation(libs.navigation.compose)
//                implementation(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlin.result)
                implementation(libs.kermit)
                implementation("org.kodein.emoji:emoji-kt:2.0.1")
                implementation("org.kodein.emoji:emoji-compose-m3:2.0.1")
                implementation("me.dvyy.syncengine:json-actions")
                api("me.dvyy.syncengine:core")
                api("me.dvyy.syncengine:client")
                api("me.dvyy.sqlite:sqlite-kt:0.0.3-alpha.2")
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
//                implementation(devNpm("copy-webpack-plugin", "9.1.0"))
//            }
//        }
    }
}

//dependencies {
//    implementation(libs.androidx.foundation.android)
//}

//sqldelight {
//    databases {
//        create("Database") {
//            packageName.set("me.dvyy.tasks.db.client")
//            srcDirs("src/commonMain/sqldelight")
//            generateAsync.set(true)
//        }
//    }
//}
