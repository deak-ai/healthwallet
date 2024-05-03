plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "ai.deak.shw"
version = "1.0.0"
application {
    mainClass.set("ai.deak.shw.ApplicationKt")
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
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    
}