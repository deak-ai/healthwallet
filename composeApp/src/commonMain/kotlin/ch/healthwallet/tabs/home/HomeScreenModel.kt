package ch.healthwallet.tabs.home

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

class HomeScreenModel(
    private val httpClient: HttpClient,
    private val appPrefsRepo: AppPrefsRepository
):ScreenModel {

    private val _appPrefs = MutableStateFlow(AppPrefs())
    val appPrefs: StateFlow<AppPrefs> = _appPrefs.asStateFlow()

    private val _vcList = MutableStateFlow(emptyList<VerifiedCredential>())
    val vcList: StateFlow<List<VerifiedCredential>> = _vcList.asStateFlow()

    init {
        println("HomeScreenModel: Initialising... ")
        screenModelScope.launch {
            appPrefsRepo.settings.collect {
                _appPrefs.value = it
                fetchVerifiableCredentials(it)
            }
        }
    }

    fun refresh() {
        screenModelScope.launch {
            fetchVerifiableCredentials(appPrefs.value)
        }
    }

    // TODO: this is a quick "hack" without  to get the app to work, will need improvements
    private suspend fun fetchVerifiableCredentials(appPrefs: AppPrefs) {
        val repo:WaltIdWalletRepository =
            WaltIdWalletRepositoryImpl(httpClient, appPrefs.waltIdWalletApi)
        val loginResult = repo.login(
            LoginRequest(
                email = appPrefs.waltIdEmail,
                password = appPrefs.waltIdPassword
            )
        )
        if (loginResult.isSuccess) {
            val walletsResult = repo.getWallets()
            val walletList = walletsResult.getOrThrow()
            val walletId = walletList.wallets.first().id
            val queryResult = repo.queryCredentials(CredentialsQuery(walletId))
            _vcList.update {
                queryResult.getOrThrow()
            }
        } else {
            throw loginResult.exceptionOrNull()!!
        }
    }

    override fun onDispose() {
        super.onDispose()
        println("HomeScreenModel: Disposing... ")
    }
}