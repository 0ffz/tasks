plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jib)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

dependencies {
    implementation(project(":app-model"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation.jvm)

    implementation(libs.kotlinx.datetime)

    implementation(libs.h2)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.ldap)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.logback)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.koin.core)

}

jib {
    to.image = "ghcr.io/0ffz/tasks-server"
    container {
        ports = listOf("4000")
        mainClass = "io.ktor.server.netty.EngineMain"
        creationTime = "USE_CURRENT_TIMESTAMP"

        // good defaults intended for Java 8 (>= 8u191) containers
//        jvmFlags = listOf(
//            "-server",
//            "-Djava.awt.headless=true",
//            "-XX:InitialRAMFraction=2",
//            "-XX:MinRAMFraction=2",
//            "-XX:MaxRAMFraction=2",
//            "-XX:+UseG1GC",
//            "-XX:MaxGCPauseMillis=100",
//            "-XX:+UseStringDeduplication"
//        )
    }
}
