package ch.healthwallet.web.plugins

import ch.healthwallet.web.routes.medicationRouting
import ch.healthwallet.web.routes.patientRouting
import ch.healthwallet.web.routes.utilsRouting
import ch.healthwallet.web.routes.vcRouting
import ch.healthwallet.web.routes.vpRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import java.sql.SQLException

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        get("/") {
            call.respondText("Welcome to the HealthSSI PIS API")
        }
        patientRouting()
        medicationRouting()
        vcRouting()
        vpRouting()
        utilsRouting()
    }
    install(StatusPages) {
        exception<SQLException> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError,
                "Database error occurred: ${cause.localizedMessage}")
        }
        exception<MissingFieldException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,
                "Missing fields on request: ${cause.localizedMessage}")
        }
    }
}