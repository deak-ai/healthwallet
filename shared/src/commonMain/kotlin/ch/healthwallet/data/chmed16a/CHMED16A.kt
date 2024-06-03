package ch.healthwallet.data.chmed16a

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
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

@Serializable
data class MedicationDTO(
    @SerialName("MedType")
    val medType: Int = 3, // default is 3: Prescription (Rx)
    @SerialName("Id")
    val medId: String? = null,
    @SerialName("Auth")
    val author: String,
    @SerialName("Zsr")
    val zsrNumber: String? = null,
    @SerialName("Dt")
    val creationDate: String? = null,
    @SerialName("Rmk")
    val remarks: String? = null,
    @SerialName("Medicaments")
    val medicaments: List<MedicamentDTO>,
    @SerialName("Patient")
    val patient: PatientDTO? = null,
)


@Serializable
data class PatientDTO(
    @SerialName("FName")
    val firstName: String,
    @SerialName("LName")
    val lastName: String,
    @SerialName("BDt")
    val birthDate: String, // Format: yyyy-mm-dd (ISO 86018 Date
    @SerialName("Gender")
    val gender: Int? = null, // 1: Male, 2: Female
    @SerialName("Ids")
    val ids : List<PatientIdDTO>? = null
)

@Serializable
data class PatientIdDTO(
    @SerialName("Type")
    val type: Int,
    @SerialName("Val")
    val value: String
)

@Serializable
data class MedicamentDTO(
    @SerialName("Id")
    val medId: String, // If IdType: ‘None’ then free text description.
    @SerialName("IdType")
    val medIdType: Int  // 1: None, 2: GTIN, 3: Pharmacode, 4: Product number (not for Rx)
)
