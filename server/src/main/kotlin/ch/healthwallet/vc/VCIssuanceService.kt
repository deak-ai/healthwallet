package ch.healthwallet.vc

import ch.healthwallet.repo.SwissEidDetail
import java.util.UUID

interface VCIssuanceService {

    suspend fun issuePrescriptionVc(mediationUUID: UUID): Result<String>

    suspend fun issueHealthInsuranceCard(firstName: String,
                                         lastName: String,
                                         dateOfBirth: String,
                                         socialSecurityNumber: String,
                                         cardNumber: String,
                                         issuerNumber: String,
                                         issuerName: String): Result<String>

    suspend fun issueSwissEid(swissEidDetail: SwissEidDetail): Result<String>


}