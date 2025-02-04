package ch.healthwallet.repo

import ch.healthwallet.data.chmed16a.MedicationDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OpenId4VcJwtIssueRequest(
    open val mapping: Mapping = Mapping()
)

@Serializable
data class SwissMedicalPrescriptionIssueRequest(
    val credentialConfigurationId: String = "SwissMedicalPrescription_jwt_vc_json",
    val credentialData: SwissMedicalPrescriptionCredentialData,
    val issuerKey: IssuerKey,
    val issuerDid: String
) : OpenId4VcJwtIssueRequest()

@Serializable
data class HealthInsuranceCardIssueRequest(
    val credentialConfigurationId: String = "HealthInsuranceCard_jwt_vc_json",
    val credentialData: HealthInsuranceCardCredentialData,
    val issuerKey: IssuerKey,
    val issuerDid: String
) : OpenId4VcJwtIssueRequest()

@Serializable
data class SwissEidIssueRequest(
    val credentialConfigurationId: String = "SwissEid_jwt_vc_json",
    val credentialData: SwissEidCredentialData,
    val issuerKey: IssuerKey,
    val issuerDid: String
) : OpenId4VcJwtIssueRequest()

@Serializable
sealed class CredentialDataV1Base(
    @SerialName("@context")
    open val context: List<String> = listOf("https://www.w3.org/2018/credentials/v1"),
    open val id: String = "[INSERT VC UUID]",
    open val issuer: Issuer = Issuer(),
    open val issuanceDate: String = "[INSERT DATE-TIME FROM WHEN THE VC IS VALID]",
    open val issued: String = "[INSERT DATE-TIME WHEN THIS VC WAS ISSUED]",
    open val expirationDate: String = "[INSERT DATE-TIME UNTIL WHEN THIS VC IS VALID]",
)

@Serializable
data class Issuer(
    val id: String = "[INSERT ISSUER DID]",
    val image: String = "https://cdn-icons-png.freepik.com/512/1145/1145785.png",
    val name: String = "CHEMED16A ePrescription",
    val url: String = "https://emediplan.ch/"
)

@Serializable
data class SwissMedicalPrescriptionCredentialData(
    val type: List<String> = listOf("VerifiableCredential", "SwissMedicalPrescription"),
    val credentialSubject: SwissMedicalPrescriptionSubject
):CredentialDataV1Base()

@Serializable
data class HealthInsuranceCardCredentialData(
    val type: List<String> = listOf("VerifiableCredential", "HealthInsuranceCard"),
    val credentialSubject: HealthInsuranceCardSubject
):CredentialDataV1Base()

@Serializable
data class SwissEidCredentialData(
    val type: List<String> = listOf("VerifiableCredential", "SwissEid"),
    val credentialSubject: SwissEidSubject
)

@Serializable
data class SwissMedicalPrescriptionSubject(
    val id: String = "[INSERT SUBJECT DID]",
    val prescription: MedicationDTO
)

@Serializable
data class HealthInsuranceCardSubject(
    val id: String = "[INSERT SUBJECT DID]",
    val healthInsuranceDetail: HealthInsuranceCardDetail
)

@Serializable
data class SwissEidSubject(
    val id: String = "[INSERT SUBJECT DID]",
    val swissEidDetail: SwissEidDetail
)

@Serializable
data class HealthInsuranceCardDetail(
    val firstName: String,
    val lastName: String,
    val dataOfBirth: String,
    val socialSecurityNumber: String,
    val cardNumber: String,
    val issuerNumber: String,
    val issuerName: String,
)

@Serializable
data class SwissEidDetail(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val address: String,
    val documentId: String,
)

@Serializable
data class IssuerKey(
    val type: String = "jwk",
    val jwk: Jwk = Jwk("", "", "", "", "", "")
)

@Serializable
data class Jwk(
    val crv: String,
    val d: String,
    val kid: String,
    val kty: String,
    val x: String,
    val y: String
)

@Serializable
data class Mapping(
    val id: String = "<uuid>",
    val issuer: IssuerMapping = IssuerMapping(),
    val credentialSubject: CredentialSubjectMapping = CredentialSubjectMapping(),
    val issuanceDate: String = "<timestamp>",
    val issued: String = "<timestamp>",
    val expirationDate: String = "<timestamp-in:365d>"
)

@Serializable
data class IssuerMapping(
    val id: String = "<issuerDid>"
)

@Serializable
data class CredentialSubjectMapping(
    val id: String = "<subjectDid>"
)
