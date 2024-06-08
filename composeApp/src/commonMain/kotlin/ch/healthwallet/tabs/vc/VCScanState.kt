package ch.healthwallet.tabs.vc

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.repo.CredentialRequest
import io.ktor.http.Url

sealed class VCScanState {
    data object Initial : VCScanState()
    data class Error(val message: String) : VCScanState()

    data class AcceptPrescription(val medRefData: MedicamentRefDataDTO,
                                  val credentialRequest: CredentialRequest): VCScanState()
    data object CredentialImported: VCScanState()

    data object PresentationCompleted : VCScanState()

    data object Processing : VCScanState()

    data class SelectCredential(val prescriptions: List<VCScreenModel.PrescriptionSelection>) : VCScanState()

}