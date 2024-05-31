package ch.healthwallet.repo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
data class LoginRequest(
    val type: String = "email",
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val id: String,
    val username: String,
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val type: String = "email"
)

@Serializable
data class ErrorMessage(
    val code: String,
    val exception: String,
    val message: String,
    val status: String
)

@Serializable
data class WalletList(
    val account: String,
    val wallets: List<Wallet>
)

@Serializable
data class Wallet(
    val id: String,
    val name: String,
    val createdOn: String,
    val addedOn: String,
    val permission: String
)

@Serializable
data class DidDetail(
    val alias: String,
    val createdOn: String,
    val default: Boolean,
    val did: String,
    val document: String,
    val keyId: String
)


@Serializable
data class CredentialsQuery(
    @SerialName("id")
    val walletId: String,
    val sortBy: String = "addedOn",
    val showPending: Boolean = false,
    val showDeleted: Boolean = false,
    val sortDescending: Boolean = false
)

@Serializable
data class VerifiedCredential(
    @SerialName("id")
    val credentialId: String,
    @SerialName("wallet")
    val walletId: String,
    val addedOn: String,
    val pending: Boolean,
    val document: String,
    val parsedDocument: JsonObject
    // val deletedOn: Any,
    // val disclosures: Any,
    //  val manifest: Any,
)

data class CredentialRequest(
    val walletId: String,
    val credentialId: String
)

data class OfferRequest(
    val did: String,
    val walletId: String,
    val credentialOffer: String,
    val acceptPending: Boolean = true
)

@Serializable
data class RejectNote(
    val note: String,
)

@Serializable
data class PresentationFilter(
    @SerialName("input_descriptors")
    val inputDescriptors: List<InputDescriptor> = listOf(InputDescriptor())
)

private val json = Json {
    encodeDefaults = true
}

fun PresentationFilter.serialize(): String =
    json.encodeToString(PresentationFilter.serializer(), this)


@Serializable
data class InputDescriptor(
    val id: String = WaltIdPrefs.DEFAULT_VC_NAME,
    val format: Format = Format(),
    val constraints: Constraints = Constraints()
)

@Serializable
data class Format(
    @SerialName("jwt_vc_json")
    val jwtVcJson: JwtVcJson = JwtVcJson()
)

@Serializable
data class JwtVcJson(
    val alg: List<String> = listOf("EdDSA")
)

@Serializable
data class Constraints(
    val fields: List<Field> = listOf(Field())
)


@Serializable
data class Field(
    val path: List<String> = listOf("$.type"),
    val filter: Filter = Filter()
)

@Serializable
data class Filter(
    val type: String = "string",
    val pattern: String = WaltIdPrefs.DEFAULT_VC_NAME
)


@Serializable
data class UsePresentationRequest(
    val presentationRequest: String,
    val selectedCredentials: List<String>,
    val disclosures: Disclosures? = null
)

@Serializable
data class Disclosures(
    val additionalProp1: List<String>,
    val additionalProp2: List<String>,
    val additionalProp3: List<String>
)




