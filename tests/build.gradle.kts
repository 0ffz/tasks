plugins {
    alias(miaLibs.plugins.kotlin.multiplatform)
    alias(miaLibs.plugins.jetbrainsCompose)
    alias(miaLibs.plugins.compose.compiler)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    sourceSets {
        kotlin {
            jvm {
                testRuns["test"].executionTask.configure {
                    useJUnitPlatform()
                }
            }
        }
        // Junit
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(miaLibs.kotest.assertions)
                implementation(miaLibs.kotest.property)
                implementation(miaLibs.kotlinx.coroutines.test)
                implementation(miaLibs.kotlinx.datetime)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(miaLibs.kermit)
                implementation(miaLibs.koin.test)
                implementation(miaLibs.junit.jupiter)
                implementation(miaLibs.kotlinx.serialization.json)
                implementation(project(":app-model"))
                implementation(project(":app-client"))
                implementation(project(":app-server"))
                implementation(libs.syncengine.client)
                implementation(libs.syncengine.server)
            }
        }
    }
}
