package ch.healthwallet.web.plugins

import ch.healthwallet.vp.VerifiablePresentationManager
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import java.time.Duration


fun Application.configureWebsockets() {

    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val vpm by inject<VerifiablePresentationManager>()
        webSocket("/notifications/{stateId}") {
            val stateId = call.parameters["stateId"]
            if (stateId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "State ID not provided"))
                return@webSocket
            }

            println("New client connected for stateId: $stateId")
            vpm.addWebSocketSession(stateId, this)
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        println("Received from $stateId: $receivedText")
                        // You can handle the received text here
                    }
                }
            } catch (e: Exception) {
                println("Exception on websocket for stateId=$stateId: $e")
            } finally {
                vpm.removeWebSocketSession(stateId, this)
                println("Client disconnected: $stateId")
            }
        }


    }
}