package ch.healthwallet.tabs.vc

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.repo.CredentialRequest

sealed class VCEvent {
    data class QrCodeScanned(val qrCode: String) : VCEvent()
    data class ScanError(val message: String) : VCEvent()

    data class AcceptCredential(val credentialRequest: CredentialRequest) : VCEvent()
    data class RejectCredential(val credentialRequest: CredentialRequest) : VCEvent()

    data object Reset : VCEvent()
}
