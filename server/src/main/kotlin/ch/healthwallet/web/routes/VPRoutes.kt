package ch.healthwallet.web.routes

import ch.healthwallet.repo.VerifyRequest
import ch.healthwallet.repo.WaltIdVerifierRepository
import ch.healthwallet.vp.VerifiablePresentationManager
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.vpRouting() {
    //val verificationCallbackHandler by inject<VerificationCallbackHandler>()
    val waltIdVerifierRepository by inject<WaltIdVerifierRepository>()
    val vpm by inject<VerifiablePresentationManager>()
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
            println("Received POST on /vp/request")
            val verifyRequest = call.receive<VerifyRequest>()
            println("Parsed verifyRequest:\n$verifyRequest")
            val openId4VpAuthorizeUrl = vpm.startVerification(verifyRequest).
                getOrElse { exception ->
                    call.respondText(
                    exception.message!!,
                    status = HttpStatusCode.InternalServerError
                )
            }
            call.respond(openId4VpAuthorizeUrl)
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
            vpm.handleCallback(callbackPaylod)
            call.respond(HttpStatusCode.OK)
        }

    }
}
