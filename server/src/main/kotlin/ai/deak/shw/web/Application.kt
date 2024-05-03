package ai.deak.shw.web

import SERVER_PORT
import ai.deak.shw.web.plugins.configureOpenApi
import ai.deak.shw.web.plugins.configureRouting
import ai.deak.shw.web.plugins.configureSerialization
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