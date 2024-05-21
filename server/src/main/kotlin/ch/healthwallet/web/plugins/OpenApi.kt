package ch.healthwallet.web.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.ktor.server.application.*

fun Application.configureOpenApi() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
        }
        info {
            title = "HealthSSI Practice Information System API"
            version = "latest"
            description = "Interact with the HealthSSI PIS API"
        }
        server {
            url = "/"
            description = "HealthSSI PIS"
        }

        securityScheme("authenticated") {
            type = AuthType.API_KEY
            location = AuthKeyLocation.COOKIE
        }

        defaultUnauthorizedResponse {
            description = "Invalid authentication"
        }
    }
}
