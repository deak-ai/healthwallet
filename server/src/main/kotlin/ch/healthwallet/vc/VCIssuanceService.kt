package ch.healthwallet.vc

import java.util.UUID

interface VCIssuanceService {

    suspend fun issuePrescriptionVc(mediationUUID: UUID): Result<String>

    suspend fun issueFhirBundleVc(patientId: String): Result<String>

}