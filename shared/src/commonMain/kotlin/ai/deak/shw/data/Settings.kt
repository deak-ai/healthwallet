package ai.deak.shw.data

import kotlinx.serialization.Serializable


@Serializable
data class LoginCredentials(
    val email: String,
    val password: String
)