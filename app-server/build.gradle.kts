plugins {
    application
    alias(miaLibs.plugins.kotlin.jvm)
    alias(miaLibs.plugins.kotlinx.serialization)
    alias(miaLibs.plugins.jib)
    id("me.dvyy.sqlite.codegen")
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
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dependencies {
    implementation(project(":app-model"))
    implementation(libs.syncengine.server)
    implementation(miaLibs.kotlinx.serialization.json)
    implementation(miaLibs.kotlinx.serialization.cbor)
    implementation(miaLibs.kotlinx.coroutines.slf4j)

    implementation(miaLibs.kermit)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.serialization.protobuf)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation.jvm)

    implementation(miaLibs.kotlinx.datetime)

    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.ldap)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.websockets)
    implementation(miaLibs.logback.classic)
    implementation(miaLibs.koin.core)
    implementation(miaLibs.koin.ktor)
}

jib {
    to.image = "ghcr.io/0ffz/tasks-server:develop"
    container {
        ports = listOf("4000")
        mainClass = "io.ktor.server.netty.EngineMain"
        creationTime = "USE_CURRENT_TIMESTAMP"
        user = "1000:1000"
        workingDirectory = "/data"
        volumes = listOf("/data")

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
sqliteKt {
    register("server") {
        packageName = "me.dvyy.tasks.server.database"
    }
}