package ch.healthwallet.web.routes

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.db.RefDataRAG
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.medicationRouting() {

    val pisDbRepo by inject<PisDbRepository>()

    val refDataRAG by inject<RefDataRAG>()

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
        get("/refdata/findmed", {
            summary = "Gets a medication refdata based on substring of the medication name"
            request {
                queryParameter<String>("subString") { required = true }
            }
        }) {
            val subString = call.request.queryParameters["subString"] ?: return@get call.respondText(
                "Missing substring",
                status = HttpStatusCode.BadRequest
            )
            val refData = pisDbRepo.findMedicamentRefDataBySubstring(subString)
                if (refData.isEmpty())  {
                    return@get call.respondText(
                        "No medication refdata with substring $subString",
                        status = HttpStatusCode.NotFound
                    )
                }
            call.respond(refData)
        }
        get("/refdata/count", {
            summary = "Gets the number of items in the medication refdata embeddings table"
        }) {
            val refData = refDataRAG.countEmbeddings()
            call.respond(refData)
        }
        get("/refdata/embeddings", {
            summary = "Gets a medication embedding based on a string"
            request {
                queryParameter<String>("text") { required = true }
            }
        }) {
            val text = call.request.queryParameters["text"] ?: return@get call.respondText(
                "Missing text",
                status = HttpStatusCode.BadRequest
            )

            val embedding = refDataRAG.getEmbedding(text)
            if (embedding.isEmpty())  {
                return@get call.respondText(
                    "No medication refdata with substring $text",
                    status = HttpStatusCode.NotFound
                )
            }
            call.respond(embedding)
        }
        get("/refdata/search", {
            summary = "Gets a list of medications closest to a string"
            request {
                queryParameter<String>("text") { required = true }
            }
        }) {
            val text = call.request.queryParameters["text"] ?: return@get call.respondText(
                "Missing text",
                status = HttpStatusCode.BadRequest
            )

            val results = refDataRAG.searchSimilarMedicaments(text, 3)
            if (results.isEmpty())  {
                return@get call.respondText(
                    "No medication refdata found that is similar to $text",
                    status = HttpStatusCode.NotFound
                )
            }
            call.respond(results)
        }
    }
}
