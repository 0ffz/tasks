plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
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
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":app-model"))
                implementation(project(":app-common"))
                implementation(libs.junit)
                implementation(libs.postgresql)
                implementation(libs.hikaricp)
                implementation(libs.testcontainers.postgresql)
            }
        }
    }
}
