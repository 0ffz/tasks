plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(project.projectDir.path)
//                        add(project.rootDir.path)
//                        add(project.rootDir.path + "/common/")
//                        add(project.rootDir.path + "/web/")
//                    }
//                }
//            }
//        }
//        binaries.executable()
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":app-common"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
            }
        }
    }
}

compose.experimental {
    web.application {}
}
