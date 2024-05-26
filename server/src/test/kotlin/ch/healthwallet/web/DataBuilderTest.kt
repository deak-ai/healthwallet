package ch.healthwallet.web

import ch.healthwallet.data.chmed23a.Gender
import ch.healthwallet.data.chmed23a.HealthcarePerson
import ch.healthwallet.data.chmed23a.Medicament
import ch.healthwallet.data.chmed23a.Medication
import ch.healthwallet.data.chmed23a.Patient
import id.walt.credentials.CredentialBuilder
import id.walt.credentials.CredentialBuilderType
import id.walt.crypto.utils.JsonUtils.toJsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

class DataBuilderTest {

    val entityIdentificationNumber = "12345"
    val issuingAuthorityId = "abc"

    val proofType = "document"
    val proofLocation = "Berlin-Brandenburg"

    fun createSamplePrescription(): JsonObject {
        val patient = Patient("1", "John", "Doe", "1985-11-11", Gender.Male)
        val hcPerson = HealthcarePerson(
            "1234", "Doogie", "Hauser",
            "Tellstrasse 1", "8000", "Zürich"
        )
        val med = Medicament("Lixiana", 1)
        val medication = Medication.createPrescription("1", patient, hcPerson, med)

        // TODO: this is ugly, converting back to string, need to find a better way,
        val jsonString = Json.encodeToString(Medication.serializer(), medication)
        return Json.parseToJsonElement(jsonString).jsonObject
    }



    @Test
    fun testDataBuilder() {
        val myCustomData = mapOf(
            "rx" to createSamplePrescription()
        ).toJsonObject()



        // build a W3C V2.0 credential
        val vc = CredentialBuilder(CredentialBuilderType.W3CV11CredentialBuilder).apply {
            addContext("https://www.w3.org/ns/credentials/examples/v1") // [W3CV2 VC context, custom context]
            addType("ChMed23APrescriptionCredential") // [VerifiableCredential, ChMed23APrescriptionCredential]

                                        // credentialId = "123"
            randomCredentialSubjectUUID() // automatically generate
            issuerDid = "did:key:abc"     // possibly later overridden by data mapping during issuance
            subjectDid = "did:key:xyz"    // possibly later overridden by data mapping during issuance

            validFromNow()                // set validFrom per current time
            validFor(90.days)             // set expiration date to now + 3 months

            //useStatusList2021Revocation("https://university.example/credentials/status/3", 94567)

            useCredentialSubject(myCustomData)
            // "custom" set myCustomData
        }.buildW3C()

        print(vc.toPrettyJson())
    }
}