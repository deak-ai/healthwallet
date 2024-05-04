package ai.deak.shw.data
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val type: String = "email",
    val email: String,
    val password: String,
)