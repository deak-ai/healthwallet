package ch.healthwallet.vc

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.repo.*
import java.util.*
import kotlin.Result

class PrescriptionVCIssuanceServiceImpl(
    private val waltIdIssuerRepo: WaltIdIssuerRepository,
    private val pisDbRepo: PisDbRepository
) : PrescriptionVCIssuanceService {

    override suspend fun issuePrescriptionVc(mediationUUID: UUID): Result<String> {
        return try {

            val medicationDTO = pisDbRepo.getMedicationById(mediationUUID.toString()) ?:
                throw IllegalStateException("No prescription with id $mediationUUID")

            val issueRequest = OpenId4VcJwtIssueRequest(
                // TODO: integrate HashiCorp Vault TSE and implement DID management
                issuerKey = IssuerKey(
                    jwk = "{\"kty\":\"EC\"," +
                            "\"d\":\"LOcVh6_257_Sp7wT3QoW68aBxiTiQPvROMAgXf_OiK4\"," +
                            "\"crv\":\"P-256\"," +
                            "\"kid\":\"yBY9dQqR499tSTxcCgG8JeJ9SYsDZSafQ1404LamPQw\"," +
                            "\"x\":\"VsgZ-a8X9ZtogkUTd0sKNbQidxc2IEzpZUYBe07O3u4\"," +
                            "\"y\":\"d58TT0QtkuxOuFYN1OjNlgzEUDmWmYioAzAJdYf8ftM\"}"
                ),
                issuerDid = "did:jwk:eyJrdHkiOiJFQyIsImNydiI6IlAtMjU2Iiwia2lkIjoieUJ" +
                        "ZOWRRcVI0OTl0U1R4Y0NnRzhKZUo5U1lzRFpTYWZRMTQwNExhbVBRdyIsIng" +
                        "iOiJWc2daLWE4WDladG9na1VUZDBzS05iUWlkeGMySUV6cFpVWUJlMDdPM3U0I" +
                        "iwieSI6ImQ1OFRUMFF0a3V4T3VGWU4xT2pObGd6RVVEbVdtWWlvQXpBSmRZZjhmdE0ifQ",
                credentialData = CredentialDataV1(
                    credentialSubject = CredentialSubject(
                        prescription = medicationDTO
                    )
                )
            )
            return waltIdIssuerRepo.openId4VcJwtIssue(issueRequest)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }


}