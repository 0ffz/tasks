plugins {
    alias(miaLibs.plugins.kotlin.multiplatform)
    alias(miaLibs.plugins.kotlinx.serialization)
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
                compileOnly("com.github.skydoves:compose-stable-marker:1.0.7")
                implementation(miaLibs.kotlinx.datetime)
                implementation(miaLibs.kermit)
                implementation(miaLibs.kotlinx.coroutines.core)
                implementation(miaLibs.kotlinx.serialization.json)
                implementation(libs.kodein.di)
                api(libs.syncengine.core)
                api(libs.syncengine.json.actions)
            }
        }
    }
}


//sqliteKt {
//    create("main") {
//        packageName = "me.dvyy.tasks.model.database"
//    }
//}