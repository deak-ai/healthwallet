package ch.healthwallet.data.chmed16a

import ch.healthwallet.repo.VerifiableCredential
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


@Serializable
data class MedicamentRefDataDTO(
    val atype: String,
    val gtin: String,
    val swmcAuthnr: String,
    val nameDe: String,
    val nameFr: String,
    val atc: String? = null,
    val authHolderName: String,
    val authHolderGln: String? = null,
    val date: String
)

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
