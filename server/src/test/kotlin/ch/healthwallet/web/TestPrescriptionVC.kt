package ch.healthwallet.web

import ch.healthwallet.data.chmed23a.Gender
import ch.healthwallet.data.chmed23a.HealthcarePerson
import ch.healthwallet.data.chmed23a.Medicament
import ch.healthwallet.data.chmed23a.Medication
import ch.healthwallet.data.chmed23a.Patient
import kotlinx.serialization.json.Json
import org.junit.Test

class TestPrescriptionVC {

    @Test
    fun `test prescription serialization`() {

        val patient = Patient("1", "John", "Doe", "1985-11-11", Gender.Male)
        val hcPerson = HealthcarePerson(
            "1234", "Doogie", "Hauser",
            "Tellstrasse 1", "8000", "Zürich"
        )
        val med = Medicament("Lixiana", 1)
        val medication = Medication.createPrescription("1", patient, hcPerson, med)
        val prettyJson = Json { prettyPrint = true }
        print(prettyJson.encodeToString(Medication.serializer(), medication))





    }
}