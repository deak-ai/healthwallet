package ai.deak.shw.web.plugins

import Greeting
import ai.deak.shw.web.routes.patientRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        patientRouting()
    }
}