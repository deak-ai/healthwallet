package ch.healthwallet.repo
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


// TODO: implement
fun extractPrescription(vsc: VerifierStatusCallback):String? {
     vsc.policyResults.results.forEach {
         if(isSwissMedicalPrescription(it)) {
             val prescription = it.policies[0].result
                 .get("vc")?.jsonObject
                 ?.get("credentialSubject")?.jsonObject
                 ?.get("prescription")?.jsonObject


         }
     }
    return "TODO"
}

fun isSwissMedicalPrescription(r: Result):Boolean {
    return r.credential == AppPrefs.DEFAULT_VC_NAME
}

