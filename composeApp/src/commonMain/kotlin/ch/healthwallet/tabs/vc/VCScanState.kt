package ch.healthwallet.tabs.vc

import ch.healthwallet.repo.CredentialRequest

sealed class VCScanState {
    data object Initial : VCScanState()
    data class Error(val message: String) : VCScanState()
    data class ConfirmOffer(val offerInfo: String) : VCScanState()
    data class ConfirmCredential(val credentialRequest: CredentialRequest): VCScanState()
    data object CredentialImported: VCScanState()
    data object Processing : VCScanState()
}