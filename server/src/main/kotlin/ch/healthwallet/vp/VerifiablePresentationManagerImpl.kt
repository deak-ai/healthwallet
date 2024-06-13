package ch.healthwallet.vp

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.repo.PrescriptionData
import ch.healthwallet.repo.VerifiableCredential
import ch.healthwallet.repo.VerifierStatusCallback
import ch.healthwallet.repo.VerifyRequest
import ch.healthwallet.repo.WaltIdVerifierRepository
import ch.healthwallet.repo.extractPrescription
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class VerifiablePresentationManagerImpl(
    private val waltIdVerifierRepository: WaltIdVerifierRepository,
    private val pisDbRepository: PisDbRepository

):VerifiablePresentationManager {

    private val verificationListeners = ConcurrentHashMap<String,
            CopyOnWriteArrayList<WebSocketSession>>()
    override suspend fun startVerification(verifyRequest: VerifyRequest): Result<String> {
        val openId4VpAuthorizeUrl = waltIdVerifierRepository.verify(verifyRequest).getOrThrow()

        val stateId = openId4VpAuthorizeUrl.parameters["state"]

        stateId?.let {
          verificationListeners.putIfAbsent(stateId, CopyOnWriteArrayList())
        }?: println("No stateId on verification url")

        return Result.success(openId4VpAuthorizeUrl.toString())
    }

    override fun addWebSocketSession(stateId: String, session: WebSocketSession) {
        verificationListeners[stateId]?.add(session)
        println("Client connected for stateId=$stateId: ${session.hashCode()}")

    }

    override fun removeWebSocketSession(stateId: String, session: WebSocketSession) {
        verificationListeners[stateId]?.remove(session)
        println("Client disconnected for stateId=$stateId: ${session.hashCode()}")
    }

    private val json = Json { ignoreUnknownKeys = true }
    override suspend fun handleCallback(callbackPayload: String) {
        try {
            val vsc = json.decodeFromString<VerifierStatusCallback>(callbackPayload)
            val pd = extractPrescription(vsc)
            if (pd != null) {
                val pde = enrichWithMedicationRefData(pd)

                val pdes = json.encodeToString(PrescriptionData.serializer(), pde)

                broadcast(pde.stateId, pdes)
            } else {
                println("No prescription found on callback")
            }
        } catch (e: Exception) {
            println("Failed to parse callback as VerifierStatusCallback: $e")
        }
    }

    private fun enrichWithMedicationRefData(pd: PrescriptionData): PrescriptionData {
        val gtin = pd.medicationId
        return gtin?.let {
            val medicationRefData = pisDbRepository.findMedicamentRefDataByGTIN(gtin)
            medicationRefData?.let {
                pd.copy(medicationRefData = medicationRefData)
            }
        } ?: pd
    }


    override suspend fun broadcast(stateId: String, message: String) {
        val ws = verificationListeners[stateId]
        ws?.forEach { session ->
            session?.let {
                try {
                    it.send(Frame.Text(message))
                } catch (e: Exception) {
                    removeWebSocketSession(stateId, session)
                    println("Removed broken connection: ${it.hashCode()}")
                }
            }
        }
    }

    fun extractMedicamentId(vc: VerifiableCredential):String? {
        return vc.parsedDocument["credentialSubject"]?.jsonObject
            ?.get("prescription")?.jsonObject
            ?.get("Medicaments")?.jsonArray
            ?.get(0)?.jsonObject
            ?.get("Id")?.jsonPrimitive
            ?.content
    }

    fun isPrescription(vc: VerifiableCredential):Boolean {
        return vc.parsedDocument["credentialSubject"]?.jsonObject
            ?.get("prescription") != null
    }

}