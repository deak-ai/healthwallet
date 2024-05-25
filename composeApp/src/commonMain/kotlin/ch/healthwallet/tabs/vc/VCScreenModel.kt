package ch.healthwallet.tabs.vc

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.http.Parameters
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VCScreenModel(

):ScreenModel {

    private val _state = MutableStateFlow<VCScanState>(VCScanState.Initial)
    val state: StateFlow<VCScanState> = _state

    fun handleEvent(event: VCEvent) {
        when (event) {
            is VCEvent.QrCodeScanned -> handleQrCodeScanned(event.qrCode)
            is VCEvent.ScanError -> _state.value = VCScanState.Error(event.message)
            is VCEvent.UseCredentialOffer -> confirmOffer(event.offer)
            is VCEvent.RejectCredentialOffer -> _state.value = VCScanState.Initial
            is VCEvent.AcceptCredential -> acceptCredential()
            is VCEvent.RejectCredential -> rejectCredential()
            is VCEvent.BackHome -> backHome()
        }
    }


    private fun backHome() {
        screenModelScope.launch {
            _state.value = VCScanState.Initial
        }
    }


    private fun handleQrCodeScanned(qrCode: String) {
        screenModelScope.launch {
            try {
                val offer = parseQrCodeString(qrCode)
                _state.value = VCScanState.ConfirmOffer(offer)
            } catch (e: IllegalArgumentException) {
                _state.value = VCScanState.Error(e.message ?: "Invalid QR Code")
            }
        }
    }

    private fun confirmOffer(offer: String) {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Obtaining prescription...")
                delay(1000)
                println("Obtained prescription")
                _state.value = VCScanState.ConfirmCredential("Triatec")

            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun acceptCredential() {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Importing prescription")
                delay(1000)
                println("Imported prescription")
                _state.value = VCScanState.CredentialImported
            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun rejectCredential() {
        screenModelScope.launch {
            _state.value = VCScanState.Processing
            try {
                println("Rejecting prescription")
                delay(1000)
                println("Rejected prescription")
                _state.value = VCScanState.Initial
            } catch (e: Exception) {
                _state.value = VCScanState.Error(e.message ?: "Processing Error")
            }
        }
    }

    private fun parseQrCodeString(qrCode: String):String {
        val url = Url(qrCode)
        val protocol = url.protocol.name
        val offer = getOffer(url.parameters)
        if (protocol != "openid-credential-offer" || offer == null) {
            throw IllegalArgumentException(
                "QR Code is not a valid OpenID for VC Credential Offer")
        }
        return offer
    }

    private fun getOffer(params: Parameters): String? {
        return params["credential_offer_uri"] ?:
        params["credential_offer"]
    }

}