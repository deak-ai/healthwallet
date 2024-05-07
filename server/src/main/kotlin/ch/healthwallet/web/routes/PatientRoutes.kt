package ch.healthwallet.web.routes

import ch.healthwallet.data.chmed23a.Gender
import ch.healthwallet.data.chmed23a.Patient
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val patientStorage = mutableListOf<Patient>(
    Patient("1", "Joe", "Bloggs", "1990-01-02", Gender.Male)
)

fun Route.patientRouting() {
    route("/patient", {
        tags = listOf("Patient CRUD")
    }) {
        get {
            if (patientStorage.isNotEmpty()) {
                call.respond(patientStorage)
            } else {
                call.respondText("No patients found", status = HttpStatusCode.OK)
            }
        }
        get("{id?}", {
            summary = "Gets a patient  based on the patient id"
            request {
                queryParameter<String>("id") { required = true }
            }
        }) {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val patient =
                patientStorage.find { it.id == id } ?: return@get call.respondText(
                    "No patient with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(patient)
        }
        post("",{
            summary = "Create a new patient"
            description = "Creates a new electronic patient record."

            request {
                body<Patient> {
                    description = "Create a new patient"
                    example("Sample Patient",
                        PatientExamples.createPatientDefaultExample
                    )
                    required = true
                }
            }

            response {
                "200" to {
                    description = "Issuer onboarding response"
                    body<Patient> {
                        example(
                            "Sample Patient",
                            PatientExamples.createPatientDefaultExample,
                        )
                    }
                }
            }
        }) {
            val patient = call.receive<Patient>()
            patientStorage.add(patient)
            call.respond(HttpStatusCode.Created, patient)
            //call.respondText("Patient stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (patientStorage.removeIf { it.id == id }) {
                call.respondText("Patient removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
