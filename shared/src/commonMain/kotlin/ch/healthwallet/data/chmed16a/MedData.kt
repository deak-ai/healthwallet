package ch.healthwallet.data.chmed16a

import kotlinx.serialization.Serializable

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



