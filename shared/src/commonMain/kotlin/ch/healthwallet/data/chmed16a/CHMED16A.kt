package ch.healthwallet.data.chmed16a

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class MedicationDTO(
    @SerialName("Patient")
    val patient: PatientDTO,
    @SerialName("Medicaments")
    val medicaments: List<MedicamentDTO>,
    @SerialName("MedType")
    val medType: Int = 3, // 1: MedicationPlan, 2: PolymedicationCheck (PMC), 3: Prescription (Rx)
    @SerialName("Id")
    val medId: String,
    @SerialName("Auth")
    val author: String,
    @SerialName("Zsr")
    val zsrNumber: String?,
    @SerialName("Dt")
    val creationDate: String,
    @SerialName("Rmk")
    val remarks: String?
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
    val gender: Int? // 1: Male, 2: Female
)

@Serializable
data class MedicamentDTO(
    @SerialName("Id")
    val medicamentId: String, // If IdType: ‘None’ then free text description.
    @SerialName("IdType")
    val medicamentIdType: Int  // 1: None, 2: GTIN, 3: Pharmacode, 4: Product number (not for Rx)
)
