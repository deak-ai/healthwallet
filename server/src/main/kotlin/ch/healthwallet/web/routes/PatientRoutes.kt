package ch.healthwallet.web.routes

import ch.healthwallet.data.chmed16a.PatientDTO
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


fun Route.patientRouting() {

    val pisDbRepo by inject<PisDbRepository>()

    route("/patient", {
        tags = listOf("Patient API")
    }) {
        get {
            val patients = pisDbRepo.getPatients()
            if (patients.isNotEmpty()) {
                call.respond(patients)
            } else {
                call.respondText("No patients found", status = HttpStatusCode.OK)
            }
        }
        get("/{id}", {
            summary = "Gets a patient based on the patient id"
            request {
                pathParameter<String>("id") { required = true }
            }
        }) {
            val id = call.parameters.get("id") ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val patient = pisDbRepo.getPatient(id.toInt()) ?: return@get call.respondText(
                    "No patient with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(patient)
        }
        post("",{
            summary = "Create a new patient"
            description = "Creates a new electronic patient record."

            request {
                body<PatientDTO>() {
                    description = "Request to create a new patient"
                    example("Sample request without insurance card number",
                        PatientExamples.createPatientWithoutInsuranceCardNumberRequest
                    )
                    example("Sample request with insurance card number",
                        PatientExamples.createPatientWithInsuranceCardNumberRequest
                    )
                    required = true
                }
            }
            response {
                HttpStatusCode.Created to {
                    description = "Patient created response with system id (type = 2)"
                    body<PatientDTO> {
                        example("Sample response without insurance card number",
                            PatientExamples.createPatientWithoutInsuranceCardNumberResponse
                        )
                        example("Sample response with insurance card number",
                            PatientExamples.createPatientWithInsuranceCardNumberResponse
                        )
                    }
                }
            }
        }) {
            val patientRequest = call.receive<PatientDTO>()
            val patientResponse = pisDbRepo.createOrUpdatePatient(patientRequest)
            call.respond(HttpStatusCode.Created, patientResponse)
        }
//        delete("{id?}") {
//            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (patientStorage.removeIf { it.id == id }) {
//                call.respondText("Patient removed correctly", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("Not Found", status = HttpStatusCode.NotFound)
//            }
//        }
    }
}
