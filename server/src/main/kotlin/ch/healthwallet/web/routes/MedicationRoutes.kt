package ch.healthwallet.web.routes

import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.db.PisDbRepository
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.medicationRouting() {

    val pisDbRepo by inject<PisDbRepository>()

    route("/medication", {
        tags = listOf("Medication API")
    }) {
        get {
            val medications = pisDbRepo.getMedications()
            if (medications.isNotEmpty()) {
                call.respond(medications)
            } else {
                call.respondText("No patients found", status = HttpStatusCode.OK)
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
            val patient = pisDbRepo.getMedicationById(id) ?: return@get call.respondText(
                    "No medication with ID $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(patient)
        }
        post("",{
            summary = "Create a new medication prescription"
            request {
                body<MedicationDTO>() {
                    description = "Request to create a new Medication prescription"
                    example("Sample Medication prescription request with a new patient",
                        MedicationExamples.createMedicationWithNewPatientRequest
                    )
                    required = true
                }
            }
            response {
                HttpStatusCode.Created to {
                    description = "Patient created response with system id (type = 2)"
                    body<MedicationDTO> {
                        example("Sample Medication prescription response with a new patient",
                            MedicationExamples.createMedicationWithNewPatientResponse
                        )
                    }
                }
            }
        }) {
            val medicationRequest = call.receive<MedicationDTO>()
            val medicationResponse = pisDbRepo.createMedication(medicationRequest)
            call.respond(HttpStatusCode.Created, medicationResponse)
        }
    }
}
