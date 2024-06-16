package ch.healthwallet.web.routes

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Route.staticsRouting() {

    route("/static", {
        tags = listOf("Statics API")
    }) {
        get("/privacy") {
            val pp = readFile("config/healthssi-privacy-policy_de.pdf")
            call.respondBytes(pp, ContentType.Application.Pdf, HttpStatusCode.OK)
        }
    }
}

fun readFile(filePath: String): ByteArray {
    return File(filePath).readBytes()
}
