plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
                implementation(libs.kotlinx.serialization.kaml)
                implementation(libs.nitrite.core)
                implementation(libs.nitrite.mvstore)
                implementation(libs.koin.core)
            }
        }
    }
}
