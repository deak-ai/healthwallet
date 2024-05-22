plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "ch.healthwallet"
version = "1.0.0"
application {
    mainClass.set("ch.healthwallet.web.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.waltid.verifiable.credentials)
    implementation(libs.ktor.swagger.ui)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.exposed.core)
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgres.jdbc)
    implementation(libs.h2.database)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}