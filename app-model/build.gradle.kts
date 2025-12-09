plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("me.dvyy.sqlite.codegen")
}

kotlin {
    applyDefaultHierarchyTemplate()
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    jvm()

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
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
                api("me.dvyy.syncengine:core")
            }
        }
    }
}


//sqliteKt {
//    create("main") {
//        packageName = "me.dvyy.tasks.model.database"
//    }
//}