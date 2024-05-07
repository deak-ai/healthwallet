package ch.healthwallet.web

import SERVER_PORT
import ch.healthwallet.web.plugins.configureOpenApi
import ch.healthwallet.web.plugins.configureRouting
import ch.healthwallet.web.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
    configureOpenApi()
}