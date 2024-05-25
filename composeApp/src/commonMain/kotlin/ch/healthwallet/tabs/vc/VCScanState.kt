package ch.healthwallet.tabs.vc

sealed class VCScanState {
    data object Initial : VCScanState()
    data class Error(val message: String) : VCScanState()
    data class ConfirmOffer(val offerInfo: String) : VCScanState()
    data class ConfirmCredential(val credentialInfo: String): VCScanState()
    data object CredentialImported: VCScanState()
    data object Processing : VCScanState()
}