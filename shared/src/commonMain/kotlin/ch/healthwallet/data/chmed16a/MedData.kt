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
    val date: String,
    val similarityScore: Double? = null,
)

@Serializable
data class MedicamentResult(
    val id: Int,
    val dt: String,
    val atype: String,
    val gtin: String,
    val swmcAuthnr: String,
    val nameDe: String,
    val nameFr: String,
    val atc: String?,
    val authHolderName: String,
    val authHolderGln: String?,
    val similarityScore: Double
)



