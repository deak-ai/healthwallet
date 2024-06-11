package ch.healthwallet.repo
import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class VerifyRequest(
    val authorizeBaseUrl:String = "openid4vp://authorize",
    val responseMode:String = "direct_post",
    val successRedirectUri: String? = null,
    val errorRedirectUri: String? = null,
    val statusCallbackUri: String? = null,
    val statusCallbackApiKey: String? = null,
    val stateId: String? = null,
    val presentationDefinition: PresentationDefinition = PresentationDefinition()
)

@Serializable
data class PresentationDefinition(
    @SerialName("request_credentials")
    val requestCredentials: List<String> = listOf(AppPrefs.DEFAULT_VC_NAME)
)


@Serializable
data class VerifierStatusCallback(
    val id: String,
    val policyResults: PolicyResults,
    val verificationResult: Boolean
)

@Serializable
data class PolicyResults(
    @SerialName("policies_failed")
    val policiesFailed: Int,
    @SerialName("policies_run")
    val policiesRun: Int,
    @SerialName("policies_succeeded")
    val policiesSucceeded: Int,
    val results: List<Result>,
    val success: Boolean,
    val time: String
)

@Serializable
data class Result(
    val credential: String,
    val policies: List<Policy>
)

@Serializable
data class Policy(
    val description: String,
    @SerialName("is_success")
    val isSuccess: Boolean,
    val policy: String,
    val result: JsonObject
)


@Serializable
data class PrescriptionData(
    val stateId: String,
    val issuanceDate: String?,
    val expirationDate: String?,
    val doctor: String?,
    val doctorDid: String?,
    val patientFirstName: String?,
    val patientLastName: String?,
    val patientBirthDate: String?,
    val patientDid: String?,
    val prescriptionId: String?,
    val medicationId: String?,
    val medicationRefData: MedicamentRefDataDTO?,
    val verificationSuccess: Boolean = false
)

// TODO: implement
fun extractPrescription(vsc: VerifierStatusCallback):PrescriptionData? {
    vsc.policyResults.results.forEach {
         if(isSwissMedicalPrescription(it)) {
             val vc = it.policies[0].result
                 .get("vc")?.jsonObject

             val issuerDid = vc?.get("issuer")?.jsonObject
                 ?.get("id")?.jsonPrimitive?.content

             val issuanceDate = vc?.get("issuanceDate")?.jsonPrimitive?.content
             val expirationDate = vc?.get("expirationDate")?.jsonPrimitive?.content

             val credentialSubject = vc
                 ?.get("credentialSubject")?.jsonObject

             val patientDid = credentialSubject?.get("id")?.jsonPrimitive?.content

             val prescription = credentialSubject
                 ?.get("prescription")?.jsonObject

             val doctor = prescription?.get("Auth")?.jsonPrimitive?.content

             val prescriptionId = prescription?.get("Id")?.jsonPrimitive?.content

             val patient = prescription?.get("Patient")?.jsonObject
             val patientFirstName = patient?.get("FName")?.jsonPrimitive?.content
             val patientLastName = patient?.get("LName")?.jsonPrimitive?.content
             val patientBirthDate = patient?.get("BDt")?.jsonPrimitive?.content

             val medicationId = prescription?.get("Medicaments")?.jsonArray?.get(0)
                 ?.jsonObject?.get("Id")?.jsonPrimitive?.content

             return PrescriptionData(
                 stateId = vsc.id,
                 issuanceDate = issuanceDate,
                 expirationDate = expirationDate,
                 doctor = doctor,
                 doctorDid = issuerDid,
                 patientFirstName = patientFirstName,
                 patientLastName = patientLastName,
                 patientBirthDate = patientBirthDate,
                 patientDid = patientDid,
                 prescriptionId = prescriptionId,
                 medicationId = medicationId,
                 medicationRefData = null, // to be enriched later
                 verificationSuccess = vsc.policyResults.success
             )
         }
     }
    return null
}

fun isSwissMedicalPrescription(r: Result):Boolean {
    return r.credential == AppPrefs.DEFAULT_VC_NAME
}

