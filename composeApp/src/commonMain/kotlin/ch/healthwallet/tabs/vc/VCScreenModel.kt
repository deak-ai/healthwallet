package ch.healthwallet.tabs.vc

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import ch.healthwallet.repo.CredentialRequest
import ch.healthwallet.repo.OfferRequest
import ch.healthwallet.repo.PresentationFilter
import ch.healthwallet.repo.UsePresentationRequest
import ch.healthwallet.repo.WalletList
import ch.healthwallet.repo.WaltIdWalletRepository
import io.ktor.http.Parameters
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val QR_ERROR = "QR Code is not a valid OpenID Credential Offer or Presentation"

class VCScreenModel(
    private val waltIdWalletRepo: WaltIdWalletRepository
):ScreenModel {

    private val _state = MutableStateFlow<VCScanState>(VCScanState.Initial)
    val state: StateFlow<VCScanState> = _state

    fun handleEvent(event: VCEvent) {
        when (event) {
            // common events
            is VCEvent.QrCodeScanned -> handleQrCodeScanned(event.qrCode)
            is VCEvent.ScanError -> handleError(event.message)
            is VCEvent.Reset -> reset()

            // verifiable credential import events (receiving prescriptions)
            is VCEvent.UseCredentialOffer -> confirmOffer(event.offer)
            is VCEvent.AcceptCredential -> acceptCredential(event.credentialRequest)
            is VCEvent.RejectCredential -> rejectCredential(event.credentialRequest)

            // verifiable presentation events (showing prescriptions)
            is VCEvent.SelectCredential -> selectCredential(event.verifyUrl)
        }
    }

    private suspend fun getWalletId(): String {
        waltIdWalletRepo.login().getOrThrow()
        val wallets: Result<WalletList> = waltIdWalletRepo.getWallets()
        val walletId = wallets.getOrThrow().wallets.first().id
        return walletId
    }

    private fun selectCredential( verifyUrl: String) {
        screenModelScope.launch {
            
            try {
            
                val walletId = getWalletId()
                val result = waltIdWalletRepo.resolvePresentationRequest(
                     walletId, Url(verifyUrl))
                
                val url = result.getOrThrow()
                
                val pd = url.parameters["presentation_definition"]
                    ?:throw IllegalStateException("No presentation definition")
                
                // passing string version of presentation definition directly
                val vcResult = waltIdWalletRepo.matchCredentials(walletId, pd)
                
                val credentials = vcResult.getOrThrow()
                
                // using first matching credential
                // TODO: provide selection choice to user
                val useRequest = UsePresentationRequest(
                    presentationRequest = url.toString(),
                    selectedCredentials = listOf(credentials.first().credentialId)
                )
                val useResult = waltIdWalletRepo.usePresentationRequest(walletId, useRequest)
                val claimedCredentials = useResult.getOrThrow()
                println(claimedCredentials)

                _state.value = VCScanState.PresentationCompleted

                
             } catch (t: Throwable) {
                val
                        message = "Failed to present prescription VC: $t"
                println(message)
                _state.value = VCScanState.Error(message)
            }

        }
    }


    private fun handleError(message: String) {
        screenModelScope.launch {
            _state.value = VCScanState.Error(message)
        }
    }

    private fun reset() {
        screenModelScope.launch {
            _state.value = VCScanState.Initial
        }
    }

    enum class QRType {
        VC,VP
    }

    private fun handleQrCodeScanned(qrCode: String) {
        screenModelScope.launch {
            try {
                println("Checking QR code")
                val qrType = getQrCodeType(qrCode)
                when (qrType) {
                    QRType.VC -> _state.value = VCScanState.ImportCredentialAsPending(qrCode)
                    QRType.VP -> _state.value = VCScanState.ProcessPresentationRequest(qrCode)
                }
            } catch (e: IllegalArgumentException) {
                _state.value = VCScanState.Error(e.message ?: QR_ERROR)
            }
        }
    }

    private fun confirmOffer(offer: String) {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Obtaining prescription...")

                waltIdWalletRepo.login().getOrThrow()

                val (_, wallets) = waltIdWalletRepo.getWallets().getOrThrow()
                val walletId = wallets.first().id

                println("Found walletid $walletId")

                val did = waltIdWalletRepo.getDids(walletId).getOrThrow().
                    firstOrNull { it.default }?.did
                        ?: throw Exception("No default DID found")

                println("Got default DID: $did")

                val offerRequest = OfferRequest(did, walletId, offer)

                println("Building offer request: $offerRequest")
                val credentialId = waltIdWalletRepo.useOfferRequest(offerRequest).getOrThrow()
                    .first().credentialId

                println("Obtained prescription")

                _state.value = VCScanState.AcceptCredential(CredentialRequest(walletId, credentialId))

            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun acceptCredential(credentialRequest: CredentialRequest) {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Importing prescription")
                waltIdWalletRepo.acceptCredential(credentialRequest)
                println("Imported prescription")
                _state.value = VCScanState.CredentialImported
            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun rejectCredential(credentialRequest: CredentialRequest) {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Rejecting prescription")
               waltIdWalletRepo.rejectCredential(credentialRequest,
                   "Rejected by user")
                println("Rejected prescription")
                _state.value = VCScanState.Initial
            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun getQrCodeType(qrCode: String):QRType {
        val url = Url(qrCode)
        val protocol = url.protocol.name
        if (protocol == "openid-credential-offer" &&
            getCredentialOffer(url.parameters) != null) {
            return QRType.VC
        }
        if (protocol == "openid4vp" &&
            getPresentationDefinition(url.parameters) != null) {
            return QRType.VP
        }
        throw IllegalArgumentException(
            QR_ERROR
        )

    }

    private fun getCredentialOffer(params: Parameters): String? {
        return params["credential_offer_uri"] ?:
            params["credential_offer"]
    }

    private fun getPresentationDefinition(params: Parameters): String? {
        return params["presentation_definition_uri"] ?:
            params["presentation_definition"]
    }

}