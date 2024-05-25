package ch.healthwallet.tabs.home

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.prefs.AppPrefs
import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.repo.CredentialsQuery
import ch.healthwallet.repo.LoginRequest
import ch.healthwallet.repo.VerifiedCredential
import ch.healthwallet.repo.WaltIdWalletRepository
import ch.healthwallet.repo.WaltIdWalletRepositoryImpl
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.component.KoinComponent


class HomeScreenModel(
    private val httpClient: HttpClient,
    private val appPrefsRepo: AppPrefsRepository
):ScreenModel, KoinComponent {

    private val _appPrefs = MutableStateFlow(AppPrefs())
    val appPrefs: StateFlow<AppPrefs> = _appPrefs.asStateFlow()

    private val _vcList = MutableStateFlow(emptyList<VerifiedCredential>())
    val vcList: StateFlow<List<VerifiedCredential>> = _vcList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        println("HomeScreenModel: Initialising... ")
        screenModelScope.launch {
            appPrefsRepo.settings.collect { settings ->
                _appPrefs.value = settings
                if (!settings.isDefault()) {
                    fetchVerifiableCredentials(settings)
                } else {
                    setErrorMessage(AppPrefs.WALLET_SETTINGS_NOT_SET)
                }
            }
        }
    }

    fun refresh() {
        screenModelScope.launch {
            val prefs = _appPrefs.value
            if (!prefs.isDefault()) {
                fetchVerifiableCredentials(prefs)
            } else {
                appPrefsRepo.settings.collect { settings ->
                    if (!settings.isDefault()) {
                        _appPrefs.value = settings
                        fetchVerifiableCredentials(settings)
                    } else {
                        setErrorMessage(AppPrefs.WALLET_SETTINGS_NOT_SET)
                    }
                }
            }
        }
    }

    // Clear error message after it's displayed
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun setErrorMessage(message: String) {
        println(message)
        _errorMessage.value = message
    }


    private suspend fun fetchVerifiableCredentials(appPrefs: AppPrefs) {
        println("fetchVerifiableCredentials")
        val repo:WaltIdWalletRepository =
            WaltIdWalletRepositoryImpl(httpClient, appPrefs.waltIdWalletApi)
        val loginResult = repo.login(
            LoginRequest(
                email = appPrefs.waltIdEmail,
                password = appPrefs.waltIdPassword
            )
        )

        loginResult.getOrElse {
            setErrorMessage("Failed to login: ${it.message}")
            return
        }
        val walletsResult = repo.getWallets()
        val walletList = walletsResult.getOrElse {
            setErrorMessage("Failed to get wallets: ${it.message}")
            return
        }

        val walletId = walletList.wallets.first().id
        val queryResult = repo.queryCredentials(CredentialsQuery(walletId))

        val queryResponse = queryResult.getOrElse {
            setErrorMessage("Failed to query credentials: ${it.message}")
            return
        }
        _vcList.update {
            queryResponse
        }
    }

    override fun onDispose() {
        super.onDispose()
        println("HomeScreenModel: Disposing... ")
    }
}