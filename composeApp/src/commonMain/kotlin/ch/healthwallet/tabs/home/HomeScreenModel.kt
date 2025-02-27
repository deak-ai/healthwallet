package ch.healthwallet.tabs.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.repo.AppPrefs
import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.repo.CredentialsQuery
import ch.healthwallet.repo.VerifiableCredential
import ch.healthwallet.repo.WaltIdWalletRepository
import ch.healthwallet.util.RefDataCache
import ch.healthwallet.util.decodeJwtPayload
import ch.healthwallet.util.extractMedicamentId
import ch.healthwallet.util.isPrescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




class HomeScreenModel(
    private val waltIdWalletRepo: WaltIdWalletRepository,
    private val appPrefsRepo: AppPrefsRepository,
    private val refDataCache: RefDataCache
):ScreenModel {

    private val _vcList = MutableStateFlow(emptyList<MedicamentRefDataDTO>())
    val vcList: StateFlow<List<MedicamentRefDataDTO>> = _vcList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }


    private fun setErrorMessage(message: String) {
        println(message)
        _errorMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun setSnackbarMessage(message: String) {
        _snackbarMessage.value = message
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

        val credentials = queryResult.getOrElse {
            setErrorMessage("Failed to query credentials: ${it.message}")
            return
        }

        val prescriptions = filterPrescriptions(credentials)

        _vcList.update {
            prescriptions
        }
    }

    private suspend fun filterPrescriptions(credentials: List<VerifiableCredential>):
        List<MedicamentRefDataDTO> {
        return credentials.filter {
            val payload = decodeJwtPayload(it.document) ?: return@filter false
            isPrescription(payload)
        }.map {
            val payload = decodeJwtPayload(it.document) ?: return@map null
            val gtin = extractMedicamentId(payload)!!
            refDataCache.get(gtin)
        }.filterNotNull()
    }



    override fun onDispose() {
        super.onDispose()
        println("HomeScreenModel: Disposing... ")
    }
}