package ch.healthwallet.mobile.tabs.settings

import ch.healthwallet.repo.LoginRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch



class LoginCredentialsViewModel(
    private val httpClient: HttpClient
) : ScreenModel {
    var email by mutableStateOf("")
    var password by mutableStateOf("")


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
        screenModelScope.launch {
             // Authentication logic here
            println("Authenticating with email: $email and password: $password")
             val response = httpClient.post("https://wallet.walt.id/wallet-api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email = email, password = password))
            }
            println("Response status: ${response.status} with body: ${response.bodyAsText()}")
            val cookie = response.headers["Set-Cookie"] ?: ""
            println("Cookie: $cookie")
        }
    }
}