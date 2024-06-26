plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
//    alias(libs.plugins.sqldelight)
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
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)

    implementation(libs.h2)
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation(libs.logback)
    implementation("io.ktor:ktor-server-config-yaml:2.3.10")

//    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
//    implementation("org.postgresql:postgresql:42.7.3")
//    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation(libs.koin.core)

}

//sqldelight {
//    databases {
//        create("ServerDatabase") {
//            packageName.set("me.dvyy.tasks.db")
//            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
//            deriveSchemaFromMigrations.set(true)
//        }
//    }
//}
