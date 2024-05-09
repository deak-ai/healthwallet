package ch.healthwallet.tabs.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.prefs.AppPrefs
import ch.healthwallet.prefs.AppPrefsRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch


class WalletSettingsViewModel(
    private val appPrefsRepo: AppPrefsRepository
) : ScreenModel {

    private var lastPrefs: AppPrefs = AppPrefs()

    var waltidWalletApi by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    private var _settingsChanged by mutableStateOf(false)
    val settingsChanged: Boolean get() = _settingsChanged


    private var _passwordVisibility by mutableStateOf(false)
    val passwordVisibility: Boolean get() = _passwordVisibility

    fun updateWaltIdWalletApi(newValue: String) {
        waltidWalletApi = newValue
        _settingsChanged = newValue != lastPrefs.waltidWalletApi
    }

    fun updateEmail(newValue: String) {
        email = newValue
        _settingsChanged = newValue != lastPrefs.email
    }

    fun updatePassword(newValue: String) {
        password = newValue
        _settingsChanged = newValue != lastPrefs.password
    }

    fun togglePasswordVisibility() {
        _passwordVisibility = ! _passwordVisibility
    }

    init {
        println("WalletSettingsViewModel: Initializing... ")
        screenModelScope.launch {
            appPrefsRepo.settings.collect {
                lastPrefs = it
                waltidWalletApi = it.waltidWalletApi
                email = it.email
                password = it.password
            }
        }
    }


    override fun onDispose() {
        super.onDispose()
        println("WalletSettingsViewModel: Disposing... ")
    }

    fun saveSettings() {
        screenModelScope.launch {
            appPrefsRepo.saveSettings(waltidWalletApi, email, password)
            _settingsChanged = false
        }
    }

}