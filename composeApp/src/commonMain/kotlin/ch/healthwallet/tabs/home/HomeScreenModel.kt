package ch.healthwallet.tabs.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.repo.AppPrefs
import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.repo.CredentialsQuery
import ch.healthwallet.repo.VerifiedCredential
import ch.healthwallet.repo.WaltIdWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeScreenModel(
    private val waltIdWalletRepo: WaltIdWalletRepository,
    private val appPrefsRepo: AppPrefsRepository
):ScreenModel {

    private val _vcList = MutableStateFlow(emptyList<VerifiedCredential>())
    val vcList: StateFlow<List<VerifiedCredential>> = _vcList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        println("HomeScreenModel: Initialising... ")
        screenModelScope.launch {
            fetchCredentialsIfInitialised()
        }
    }

    private suspend fun fetchCredentialsIfInitialised() {
        if (!appPrefsRepo.appPrefs.value.isDefault()) {
            fetchVerifiableCredentials()
        } else {
            setErrorMessage(AppPrefs.WALLET_SETTINGS_NOT_SET)
        }
    }

    fun refresh() {
        screenModelScope.launch {
            fetchCredentialsIfInitialised()
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


    private suspend fun fetchVerifiableCredentials() {
        println("fetchVerifiableCredentials")
        val loginResult = waltIdWalletRepo.login()

        loginResult.getOrElse {
            setErrorMessage("Failed to login: ${it.message}")
            return
        }
        val walletsResult = waltIdWalletRepo.getWallets()
        val walletList = walletsResult.getOrElse {
            setErrorMessage("Failed to get wallets: ${it.message}")
            return
        }

        val walletId = walletList.wallets.first().id
        val queryResult = waltIdWalletRepo.queryCredentials(CredentialsQuery(walletId))

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