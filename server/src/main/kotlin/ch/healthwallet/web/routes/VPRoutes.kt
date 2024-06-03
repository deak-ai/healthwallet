package ch.healthwallet.web.routes

import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.qr.generateQRCode
import ch.healthwallet.repo.VerifyRequest
import ch.healthwallet.repo.WaltIdVerifierRepository
import ch.healthwallet.vc.PrescriptionVCIssuanceService
import ch.healthwallet.vc.VerificationCallbackHandler
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*


fun Route.vpRouting() {
    //val verificationCallbackHandler by inject<VerificationCallbackHandler>()
    val waltIdVerifierRepository by inject<WaltIdVerifierRepository>()

    route("/vp", {
        tags = listOf("Prescription VC Verification API")
    }) {

        post("/request", {
            summary = "Request a prescription VP"
            description = "Request a prescription verifiable presentation using OIDC4VP"
            request {
                body<VerifyRequest>() {
                    description = "A verify request"
                    example("Minimum verify request", VPExamples.minimumVPRequestWithStatusCallback)
                    example("Full verify request", VPExamples.completeVPRequestStructure)
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "OpenID4VP authorization URL"
                    body<String> {
                        example("OpenID4VP authorization URL",
                            VPExamples.openId4VpAuthorizeUrl
                        )
                    }
                }
            }
        }) {
            val verifyRequest = call.receive<VerifyRequest>()
            val openId4VpAuthorizeUrl = waltIdVerifierRepository.verify(verifyRequest).
                getOrElse { exception ->
                    call.respondText(
                    exception.message!!,
                    status = HttpStatusCode.InternalServerError
                )
            }
            call.respond(openId4VpAuthorizeUrl.toString())
        }

        post("/status", {
            summary = "Callback endpoint to receive status changes from presentation process"
            request {
                body<String>() {
                    description = "Status update"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK
            }
        }) {
            val callbackPaylod = call.receive<String>()
            println("Received callback on /vp/status")
            println("Payload:\n$callbackPaylod")
            println("Call details:\n:$call")
            call.respond(HttpStatusCode.OK)
        }

    }
}
