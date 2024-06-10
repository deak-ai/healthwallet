package ch.healthwallet.vp

import ch.healthwallet.repo.VerifyRequest
import io.ktor.websocket.WebSocketSession

interface VerifiablePresentationManager {

    suspend fun startVerification(verifyRequest: VerifyRequest): Result<String>

    fun addWebSocketSession(stateId: String, session: WebSocketSession)

    fun removeWebSocketSession(stateId: String, session: WebSocketSession)

    suspend fun broadcast(stateId: String, message: String)

}