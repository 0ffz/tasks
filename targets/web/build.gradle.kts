import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(miaLibs.plugins.kotlin.multiplatform)
    alias(miaLibs.plugins.jetbrainsCompose)
    alias(miaLibs.plugins.compose.compiler)
}

kotlin {
//    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/common/")
                        add(project.rootDir.path + "/web/")
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":app-client"))
                implementation(miaLibs.koin.core)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
//                implementation(npm("sql.js", "1.11.0"))
                implementation(npm("@sqlite.org/sqlite-wasm", "3.46.1-build3"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}
