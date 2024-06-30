plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jib)
    alias(libs.plugins.sqldelight)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app-model"))
    implementation(libs.uuid)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation.jvm)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)

    implementation(libs.h2)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.ldap)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.logback)
    implementation(libs.ktor.server.config.yaml)

    implementation(libs.postgresql)
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
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

sqldelight {
    databases {
        create("ServerDatabase") {
            packageName.set("me.dvyy.tasks.db")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
            srcDirs("src/sqldelight")
        }
    }
}
