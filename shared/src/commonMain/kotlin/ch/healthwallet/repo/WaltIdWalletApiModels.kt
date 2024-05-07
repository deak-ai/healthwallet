package ch.healthwallet.repo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

@Serializable
data class CredentialRequest(
    val walletId: String,
    val credentialId: String
)






