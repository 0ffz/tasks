plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
                implementation(libs.kotest.assertions)
                implementation(libs.kotest.property)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":app-model"))
                implementation(project(":app-common"))
                implementation(project(":app-server"))
                implementation("me.dvyy.syncengine:client")
                implementation("me.dvyy.syncengine:server")
                implementation(libs.junit)
                implementation(libs.postgresql)
                implementation(libs.hikaricp)
                implementation(libs.testcontainers.postgresql)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
