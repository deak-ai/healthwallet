package ch.healthwallet.web.routes

import ch.healthwallet.qr.generateQRCode
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
    route("/vp", {
        tags = listOf("Prescription VC Verification API")
    }) {
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
