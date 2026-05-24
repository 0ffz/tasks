import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(miaLibs.plugins.kotlin.multiplatform)
    alias(miaLibs.plugins.jetbrainsCompose)
    alias(miaLibs.plugins.compose.compiler)
    alias(miaLibs.plugins.kotlinx.serialization)
    id("com.codingfeline.buildkonfig") version "0.21.2"
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
    android {
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
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.compose.material3)
                implementation(libs.compose.material.icons.extended)
                implementation("dev.seyfarth:tabler-icons-kmp:1.0.0")
                implementation(libs.compose.ui)
                implementation(libs.rebugger)
//                implementation("app.cash.molecule:molecule-runtime:2.2.0")
//                implementation("com.mohamedrejeb.dnd:compose-dnd:0.3.0")
                implementation(libs.kotlinx.collections.immutable)
                implementation(miaLibs.kotlinx.datetime)
                implementation(miaLibs.kotlinx.serialization.json)
                implementation(miaLibs.kotlinx.serialization.cbor)
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
                implementation(miaLibs.koin.compose)
                implementation(miaLibs.koin.compose.viewmodel)
//                implementation(libs.kotlin.result)
                implementation(miaLibs.kermit)
                implementation(libs.emoji.compose)
                implementation(libs.emoji.compose.m3)
                api(libs.syncengine.json.actions)
                api(libs.syncengine.core)
                api(libs.syncengine.client)
                api(miaLibs.sqlite.kt)
                api(miaLibs.androidx.collection)
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
                implementation(miaLibs.kotlinx.coroutines.swing)
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
                implementation(miaLibs.koin.android)
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

tasks {
    assemble {
        dependsOn(generateBuildKonfig)
    }
}

buildkonfig {
    packageName = "me.dvyy.tasks"
    defaultConfigs {
        buildConfigField(STRING, "version", "v" + project.version.toString())
    }
}