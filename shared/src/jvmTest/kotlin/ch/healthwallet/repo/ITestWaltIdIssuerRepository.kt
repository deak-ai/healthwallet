package ch.healthwallet.repo

import ch.healthwallet.data.chmed16a.MedicamentDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ITestWaltIdIssuerRepository {

    companion object {
        val repo: WaltIdIssuerRepository = WaltIdIssuerRepositoryImpl(
            createHttpClient(),
            "https://issuer.healthwallet.li"
        )

        private fun createHttpClient() = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }

        }
        val issueRequest =
            OpenId4VcJwtIssueRequest(
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
                        prescription = MedicationDTO(
                            medType = 3,
                            medId = "13dc576f-e7a6-4abd-a2c5-81e3d49d8487",
                            author = "GLN123456",
                            creationDate = "2024-05-22T09:43:08+02:00",
                            medicaments = listOf(
                                MedicamentDTO(
                                    medId = "7680501410985",
                                    medIdType = 2
                                )
                            ),
                            patient = PatientDTO(
                                firstName = "Jane",
                                lastName = "Doe",
                                birthDate = "1985-05-01",
                                gender = 2
                            )
                        )
                    )
                )
            )
    }

    @Ignore
    @Test
    fun `Calling openId4VcJwtIssue with valid request returns 200 and credential offer`() {
        runTest {
            val result = repo.openId4VcJwtIssue(issueRequest)
            assertTrue(result.isSuccess)
            val credentialOffer = result.getOrNull()
            assertNotNull(credentialOffer)
            println(credentialOffer)
        }
    }

    @Test
    fun `Calling openId4VcJwtIssue with invalid issuerKey returns failure result`() {
        runTest {
            val invalidRequest = issueRequest.copy(IssuerKey(jwk = "dummy"))
            val result = repo.openId4VcJwtIssue(invalidRequest)
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertNotNull(exception)
            println(exception)
        }
    }


}