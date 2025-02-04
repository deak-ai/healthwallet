package ch.healthwallet.web.routes

import ch.healthwallet.repo.SwissEidDetail
import ch.healthwallet.vc.VCIssuanceService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*


fun Route.vcRouting() {

    val issuanceService by inject<VCIssuanceService>()

    route("/vc", {
        tags = listOf("Prescription VC Issuance API")
    }) {
        post("/issue", {
            summary = "Issue a medication prescription VC using OID4VCI"
            request {
                body<String>() {
                    description = "Request to issue a VC for a medication prescription"
                    example(
                        "UUID Example",
                        VCExamples.vcPrescriptionIssueRequest
                    )
                    required = true
                }
            }
            response {
                HttpStatusCode.Created to {
                    description = "Medication prescription VC OID4VC compliant credential offer"
                    body<String> {
                        example(
                            "Example medication prescription VC openid-credential-offer",
                            MedicationExamples.createMedicationPrescriptionResponse
                        )
                    }
                }
            }
        }) {
            val uuidString = call.receive<String>()
            val uuid: UUID
            try {
                uuid = UUID.fromString(uuidString)
            } catch (e: IllegalArgumentException) {
                return@post call.respondText(
                    "Invalid UUID",
                    status = HttpStatusCode.BadRequest
                )
            }
            val credentialOfferResult = issuanceService.issuePrescriptionVc(uuid)
            val credentialOffer = credentialOfferResult.getOrElse { exception ->
                call.respondText(
                    exception.message!!,
                    status = HttpStatusCode.InternalServerError
                )
            }
            call.respond(HttpStatusCode.OK, credentialOffer)
        }


        // ------------------------

        post("/issue/swisseid", {
            summary = "Issue a swiss e-id VC"
            description = "Issue a swiss e-id credential using OIDC4VP"
            request {
                body<SwissEidDetail>() {
                    description = "A "
                    example("Minimum verify request", VCExamples.swissEidDetailExampleRequest)
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "OpenID4VP authorization URL"
                    body<String> {
                        example("OpenID4VP authorization URL",
                            VCExamples.vcIssueResponse
                        )
                    }
                }
            }
        }) {
            println("Received POST on /vc/issue/swisseid")
            val issueRequest = call.receive<SwissEidDetail>()
            println("Parsed SwissEidDetail:\n$issueRequest")
            val credentialOfferResult = issuanceService.issueSwissEid(issueRequest)
            val credentialOffer = credentialOfferResult.getOrElse { exception ->
                call.respondText(
                    exception.message!!,
                    status = HttpStatusCode.InternalServerError
                )
            }
            call.respond(HttpStatusCode.OK, credentialOffer)
        }


    }
}
