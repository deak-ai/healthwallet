package ch.healthwallet.tabs.settings

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.prefs.AppPrefs
import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.repo.LoginRequest
import ch.healthwallet.repo.WaltIdWalletRepository
import ch.healthwallet.repo.WaltIdWalletRepositoryImpl
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class WalletSettingsScreenModel(
    private val appPrefsRepo: AppPrefsRepository,
    private val httpClient: HttpClient
) : ScreenModel {

    private var lastPrefs: AppPrefs = AppPrefs()

    private val _showStartupDialog = MutableStateFlow(false)
    val showStartupDialog: StateFlow<Boolean> = _showStartupDialog.asStateFlow()

    val waltIdAppPrefsScreenValid: Boolean get() =
        !waltidWalletApiHasErrors && !waltIdEmailHasErrors && !waltIdPasswordHasErrors

    var waltidWalletApi by mutableStateOf("")
    val waltidWalletApiHasErrors by derivedStateOf {
        if (waltidWalletApi.isNotEmpty()) {
            ! isUrlValid(waltidWalletApi)
        } else {
          true
        }
    }


    var waltIdEmail by mutableStateOf("")
    val waltIdEmailHasErrors by derivedStateOf {
        if (waltIdEmail.isNotEmpty()) {
            !isEmailValid(waltIdEmail)
        } else {
            true
        }
    }

    private fun isUrlValid(url: String) = urlRegex.matches(url)
    private fun isEmailValid(email: String) = emailRegex.matches(email)

    companion object {
        private val emailRegex =
            ("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+").toRegex()

        private val urlRegex =
            ("^(http|https)://[^\\s$.?#].[^\\s]*$").toRegex()
        //TODO: URL regex a bit too simplistic, can be improved
    }


    var waltIdPassword by mutableStateOf("")

    val waltIdPasswordHasErrors by derivedStateOf {
        if (waltIdPassword.isNotEmpty()) {
            // TODO: better password validation
            false
        } else {
            true
        }
    }


    private var _passwordVisibility by mutableStateOf(false)
    val passwordVisibility: Boolean get() = _passwordVisibility

    private var _settingsChanged by mutableStateOf(false)
    val settingsChanged: Boolean get() = _settingsChanged

    fun updateWaltIdWalletApi(newValue: String) {
        waltidWalletApi = newValue
        _settingsChanged = newValue != lastPrefs.waltIdWalletApi
    }

    fun updateWaltIdEmail(newValue: String) {
        waltIdEmail = newValue
        _settingsChanged = newValue != lastPrefs.waltIdEmail
    }

    fun updateWaltIdPassword(newValue: String) {
        waltIdPassword = newValue
        _settingsChanged = newValue != lastPrefs.waltIdPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisibility = ! _passwordVisibility
    }


    init {
        println("WalletSettingsScreenModel: Initialising... ")
        screenModelScope.launch {
            appPrefsRepo.settings.collect {
                lastPrefs = it
                waltidWalletApi = it.waltIdWalletApi
                waltIdEmail = it.waltIdEmail
                waltIdPassword = it.waltIdPassword
                _showStartupDialog.value = !it.waltIdPrefsValid
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        println("WalletSettingsScreenModel: Disposing... ")
    }

    fun saveSettings() {
        screenModelScope.launch {
            appPrefsRepo.saveSettings(waltidWalletApi, waltIdEmail, waltIdPassword)
            appPrefsRepo.setAppPrefsValid()
            _showStartupDialog.value = false
            _settingsChanged = false
        }
    }

    fun testConnection(snackbarHostState: SnackbarHostState) {
        screenModelScope.launch {
            val waltIdWalletRepo: WaltIdWalletRepository = WaltIdWalletRepositoryImpl(
                httpClient, waltidWalletApi)

            val result = waltIdWalletRepo.login(LoginRequest(email = waltIdEmail, password = waltIdPassword))

            if (result.isSuccess) {
                showSnackbar(
                    snackbarHostState,
                    "Login successful!",
                    SnackbarDuration.Long
                )
            } else {
                val error = result.exceptionOrNull()
                showSnackbar(
                    snackbarHostState,
                    "Login failed: ${error?.message}.",
                    SnackbarDuration.Indefinite)
            }
        }
    }
    private suspend fun showSnackbar(
        snackbarHostState: SnackbarHostState,
        msg: String, duration: SnackbarDuration) {
        val result = snackbarHostState
            .showSnackbar(
                message = msg,
                withDismissAction = true,
                duration = duration
            )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                // currently not used
                println("Snackbar: ActionPerformed")
            }
            SnackbarResult.Dismissed -> {
                println("Snackbar: Dismissed")
            }
        }
    }

}