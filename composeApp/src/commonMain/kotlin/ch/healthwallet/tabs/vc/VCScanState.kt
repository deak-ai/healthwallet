package ch.healthwallet.tabs.vc

import ch.healthwallet.repo.CredentialRequest

sealed class VCScanState {
    data object Initial : VCScanState()
    data class Error(val message: String) : VCScanState()

    data class ImportCredentialAsPending(val credentialOfferUrl: String) : VCScanState()
    data class AcceptCredential(val credentialRequest: CredentialRequest): VCScanState()
    data object CredentialImported: VCScanState()

    data class ProcessPresentationRequest(val verifyUrl: String) : VCScanState()

    data object PresentationCompleted : VCScanState()

    data object Processing : VCScanState()
}