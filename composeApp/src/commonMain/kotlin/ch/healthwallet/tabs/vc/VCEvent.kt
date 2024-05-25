package ch.healthwallet.tabs.vc

sealed class VCEvent {
    data class QrCodeScanned(val qrCode: String) : VCEvent()
    data class ScanError(val message: String) : VCEvent()
    data class UseCredentialOffer(val offer: String) : VCEvent()
    data object RejectCredentialOffer : VCEvent()
    data object AcceptCredential : VCEvent()
    data object RejectCredential : VCEvent()
    data object BackHome : VCEvent()
}