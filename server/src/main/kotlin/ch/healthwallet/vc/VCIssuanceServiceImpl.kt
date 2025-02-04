package ch.healthwallet.vc

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.repo.*
import java.util.*
import kotlin.Result

private const val ISSUER_DID = "did:jwk:eyJrdHkiOiJFQyIsImNydiI6IlAtMjU2Iiwia2lkIjoieUJ" +
        "ZOWRRcVI0OTl0U1R4Y0NnRzhKZUo5U1lzRFpTYWZRMTQwNExhbVBRdyIsIng" +
        "iOiJWc2daLWE4WDladG9na1VUZDBzS05iUWlkeGMySUV6cFpVWUJlMDdPM3U0I" +
        "iwieSI6ImQ1OFRUMFF0a3V4T3VGWU4xT2pObGd6RVVEbVdtWWlvQXpBSmRZZjhmdE0ifQ"

private val ISSUER_KEY = Jwk(
    crv = "P-256",
    d = "LOcVh6_257_Sp7wT3QoW68aBxiTiQPvROMAgXf_OiK4",
    kid = "yBY9dQqR499tSTxcCgG8JeJ9SYsDZSafQ1404LamPQw",
    kty = "EC",
    x = "VsgZ-a8X9ZtogkUTd0sKNbQidxc2IEzpZUYBe07O3u4",
    y = "d58TT0QtkuxOuFYN1OjNlgzEUDmWmYioAzAJdYf8ftM"
)

class VCIssuanceServiceImpl(
    private val waltIdIssuerRepo: WaltIdIssuerRepository,
    private val pisDbRepo: PisDbRepository
) : VCIssuanceService {

    override suspend fun issuePrescriptionVc(mediationUUID: UUID): Result<String> {
        return try {
            val medicationDTO = pisDbRepo.getMedicationById(mediationUUID.toString())
                ?: throw IllegalStateException("No prescription with id $mediationUUID")

            val issueRequest = SwissMedicalPrescriptionIssueRequest(
                issuerKey = IssuerKey(
                    jwk = ISSUER_KEY
                ),
                issuerDid = ISSUER_DID,
                credentialData = SwissMedicalPrescriptionCredentialData(
                    credentialSubject = SwissMedicalPrescriptionSubject(
                        id = "[INSERT SUBJECT DID]",
                        prescription = medicationDTO
                    )
                )
            )
            println("Issuing VC for prescription $mediationUUID")
            return waltIdIssuerRepo.openId4VcJwtIssue(issueRequest)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun issueHealthInsuranceCard(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        socialSecurityNumber: String,
        cardNumber: String,
        issuerNumber: String,
        issuerName: String
    ): Result<String> {
        return try {
            val issueRequest = HealthInsuranceCardIssueRequest(
                credentialData = HealthInsuranceCardCredentialData(
                    credentialSubject = HealthInsuranceCardSubject(
                        id = "[INSERT SUBJECT DID]",
                        healthInsuranceDetail = HealthInsuranceCardDetail(
                            firstName, lastName, dateOfBirth, socialSecurityNumber,
                            cardNumber, issuerNumber, issuerName)
                    )
                ),
                issuerKey = IssuerKey(
                    jwk = ISSUER_KEY
                ),
                issuerDid = ISSUER_DID
            )
            println("Issuing health insurance card for $firstName $lastName")
            return waltIdIssuerRepo.openId4VcJwtIssue(issueRequest)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun issueSwissEid(
        swissEidDetail: SwissEidDetail
    ): Result<String> {
        return try {
            val issueRequest = SwissEidIssueRequest(
                credentialData = SwissEidCredentialData(
                    credentialSubject = SwissEidSubject(
                        id = "[INSERT SUBJECT DID]",
                        swissEidDetail = swissEidDetail
                    )
                ),
                issuerKey = IssuerKey(
                    jwk = ISSUER_KEY
                ),
                issuerDid = ISSUER_DID
            )
            println("Issuing health insurance card for id: ${swissEidDetail.documentId}")
            return waltIdIssuerRepo.openId4VcJwtIssue(issueRequest)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }



}