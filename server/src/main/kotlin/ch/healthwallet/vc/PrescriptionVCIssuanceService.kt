package ch.healthwallet.vc

import java.util.UUID

interface PrescriptionVCIssuanceService {

    suspend fun issuePrescriptionVc(mediationUUID: UUID): Result<String>

}