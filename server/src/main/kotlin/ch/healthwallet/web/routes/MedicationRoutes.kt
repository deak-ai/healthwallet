package ch.healthwallet.web.routes

import ch.healthwallet.db.PisDbRepository
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.medicationRouting() {

    val pisDbRepo by inject<PisDbRepository>()

    route("/medications", {
        tags = listOf("Medication API")
    }) {
        get {
            val medications = pisDbRepo.getMedications()
            if (medications.isNotEmpty()) {
                call.respond(medications)
            } else {
                call.respondText("No medications found", status = HttpStatusCode.OK)
            }
        }
        get("/{id}", {
            summary = "Gets a medication based on its ID"
            request {
                pathParameter<String>("id") { required = true }
            }
        }) {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val medication = pisDbRepo.getMedicationById(id) ?: return@get call.respondText(
                    "No medication with ID $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(medication)
        }
        get("/refdata/{gtin}", {
            summary = "Gets a medication refdata based on its GTIN"
            request {
                pathParameter<String>("gtin") { required = true }
            }
        }) {
            val gtin = call.parameters["gtin"] ?: return@get call.respondText(
                "Missing GTIN",
                status = HttpStatusCode.BadRequest
            )
            val refData = pisDbRepo.findMedicamentRefDataByGTIN(gtin) ?: return@get call.respondText(
                "No medication refdata with GTIN $gtin",
                status = HttpStatusCode.NotFound
            )
            call.respond(refData)
        }
    }
}
