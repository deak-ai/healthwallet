package ch.healthwallet.web.routes

import ch.healthwallet.qr.generateQRCode
import ch.healthwallet.vc.PrescriptionVCIssuanceService
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


fun Route.utilsRouting() {

    route("/utils", {
        tags = listOf("Utilities API")
    }) {
        post("/qrcode", {
            summary = "Turn a string (e.g. OID4VC credential offer) into a QR code"
            request {
                body<String>() {
                    description = "Request to convert a credential offer to a QR code"
                    example(
                        "Credential Offer Example",
                        VCExamples.vcPrescriptionIssueResponse
                    )
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Medication prescription VC OID4VC compliant credential offer"
                    body<ByteArray> {}
                }
            }
        }) {
            val requestString = call.receive<String>()
            val qrBytes = generateQRCode(requestString)
            call.respondBytes(qrBytes, ContentType.Image.PNG, HttpStatusCode.OK)
        }
    }
}
