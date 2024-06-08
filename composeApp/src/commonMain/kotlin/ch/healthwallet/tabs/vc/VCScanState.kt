package ch.healthwallet.tabs.vc

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.repo.CredentialRequest

sealed class VCScanState {
    data object Initial : VCScanState()
    data class Error(val message: String) : VCScanState()

    data class ImportPrescriptionAsPending(val credentialOfferUrl: String) : VCScanState()
    data class AcceptPrescription(val medRefData: MedicamentRefDataDTO,
                                  val credentialRequest: CredentialRequest): VCScanState()
    data object CredentialImported: VCScanState()

    data class ProcessPresentationRequest(val verifyUrl: String) : VCScanState()

    data object PresentationCompleted : VCScanState()

    data object Processing : VCScanState()
}