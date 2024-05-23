package ch.healthwallet.repo

import ch.healthwallet.data.chmed16a.MedicationDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenId4VcJwtIssueRequest(
    val issuerKey: IssuerKey,
    val issuerDid: String,
    val credentialConfigurationId:String = "SwissMedicalPrescription_jwt_vc_json",
    val credentialData: CredentialDataV1,
    val mapping: Mapping = Mapping()
)

@Serializable
data class IssuerKey(
    val type: String = "jwk",
    val jwk: String
)

@Serializable
data class CredentialDataV1(
    @SerialName("@context")
    val context: List<String> = listOf("https://www.w3.org/2018/credentials/v1"),
    val id: String = "[INSERT VC UUID]",
    val type: List<String> = listOf("VerifiableCredential", "SwissMedicalPrescription"),
    val issuer: Issuer = Issuer(),
    val issuanceDate: String = "[INSERT DATE-TIME FROM WHEN THE VC IS VALID]",
    val issued: String = "[INSERT DATE-TIME WHEN THIS VC WAS ISSUED]",
    val expirationDate: String = "[INSERT DATE-TIME UNTIL WHEN THIS VC IS VALID]",
    val credentialSubject: CredentialSubject,
)



@Serializable
data class CredentialSubject(
    val id: String = "[INSERT SUBJECT DID]",
    val prescription: MedicationDTO
)

@Serializable
data class Issuer(
    val id: String = "[INSERT ISSUER DID]",
    val image: String = "https://cdn-icons-png.freepik.com/512/1145/1145785.png",
    val name: String = "CHEMED16A ePrescription",
    val url: String = "https://emediplan.ch/"
)

@Serializable
data class Mapping(
    val id: String = "<uuid>",
    val issuer: IssuerMapping = IssuerMapping(),
    val credentialSubject: CredentialSubjectMapping =
        CredentialSubjectMapping(),
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


