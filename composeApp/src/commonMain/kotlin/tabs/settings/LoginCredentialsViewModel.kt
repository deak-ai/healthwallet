package tabs.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel

class LoginCredentialsViewModel : ScreenModel {
    var email by mutableStateOf("Email")
    var password by mutableStateOf("Password")

    private var _passwordVisibility by mutableStateOf(false)
    val passwordVisibility: Boolean get() = _passwordVisibility

    fun togglePasswordVisibility() {
        _passwordVisibility = ! _passwordVisibility
    }

    init {
        println("LoginViewModel: Initializing... ")
    }

    override fun onDispose() {
        super.onDispose()
        println("LoginViewModel: Disposing... ")
    }

    fun authenticate() {
        // Authentication logic here
        println("Authenticating with email: $email and password: $password")
    }
}