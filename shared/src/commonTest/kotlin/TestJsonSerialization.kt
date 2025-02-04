
import ch.healthwallet.data.chmed16a.MedicamentDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import ch.healthwallet.repo.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.Result
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals


class TestJsonSerialization {

    private val json = Json {
        encodeDefaults = true
    }

    @Test
    fun `Test defaults serialization`() {
        val l = LoginRequest(type="email", email ="user@email.com", password = "password")
        assertEquals("email", l.type)
        val jsonString = json.encodeToString(LoginRequest.serializer(),l)
        println(jsonString)
        assertContains(jsonString, "type")
    }

    private  val ISSUER_DID = "did:jwk:eyJrdHkiOiJFQyIsImNydiI6IlAtMjU2Iiwia2lkIjoieUJ" +
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


    @Test
    fun `Test Issuance request serialization`() {

        val issueRequest = SwissMedicalPrescriptionIssueRequest(
            issuerKey = IssuerKey(
                jwk = ISSUER_KEY
            ),
            issuerDid = ISSUER_DID,
            credentialData = SwissMedicalPrescriptionCredentialData(
                credentialSubject = SwissMedicalPrescriptionSubject(
                    id = "[INSERT SUBJECT DID]",
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
        val jsonString = json.encodeToString(SwissMedicalPrescriptionIssueRequest.serializer(),issueRequest)
        println(jsonString)
    }

    @Test
    fun `Test swiss medical request serialization`() {

        val issueRequest = SwissMedicalPrescriptionIssueRequest(
            issuerKey = IssuerKey(
                jwk = ISSUER_KEY
            ),
            issuerDid = ISSUER_DID,
            credentialData = SwissMedicalPrescriptionCredentialData(
                credentialSubject = SwissMedicalPrescriptionSubject(
                    id = "[INSERT SUBJECT DID]",
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
        val jsonString = json.encodeToString(SwissMedicalPrescriptionIssueRequest.serializer(),issueRequest)
        println(jsonString)
    }

    @Serializable
    data class LoginRequest(
        val type: String = "email",
        val email: String,
        val password: String,
    )
}