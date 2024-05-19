package ch.healthwallet.db

import MedicamentDAO
import MedicamentsTable
import MedicationDAO
import MedicationsTable
import PatientDAO
import PatientsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestPisSchema {

    @BeforeAll
    fun setup() {
        Database.connect(
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(
                PatientsTable,
                MedicamentsTable,
                MedicationsTable
            )
        }
    }


    @Test
    @Order(1)
    fun `test Create operations`() {
        val newPatient = transaction {
            PatientDAO.new {
                firstName = "Jane"
                lastName = "Smith"
                birthDate = "1985-05-01"
                gender = 2
            }
        }

       assertNotNull(newPatient)

        val newMedication = transaction {
            MedicationDAO.new {
                patient = newPatient
                medType = 2
                medId = "MED456"
                author = "Dr. Johnson"
                zsrNumber = "ZSR456"
                creationDate = "2022-02-01"
                remarks = "No allergies noted."
            }
        }

        assertNotNull(newMedication)

        val newMedicament = transaction {
            MedicamentDAO.new {
                medication = newMedication
                medicamentId = "MEDIC002"
                medicamentIdType = 2
            }
        }

        assertNotNull(newMedicament)

    }


    @Test
    @Order(2)
    fun `test Read operations`() {
        transaction {
            val patient = PatientDAO.findById(1)
            assertNotNull(patient)
            assertEquals("Jane", patient.firstName)
            assertEquals("Smith", patient.lastName)

            val medication = MedicationDAO.findById(1)
            assertNotNull(medication)
            assertEquals("MED456", medication.medId)
            assertEquals("Dr. Johnson", medication.author)

            val medicament = MedicamentDAO.findById(1)
            assertNotNull(medicament)
            assertEquals("MEDIC002", medicament.medicamentId)
        }
    }

    @Test
    @Order(3)
    fun `test Update operations`() {
        transaction {
            val patient = PatientDAO.findById(1)
            assertNotNull(patient)
            patient.firstName = "Jonathan"
            assertEquals("Jonathan", patient.firstName)

            val medication = MedicationDAO.findById(1)
            assertNotNull(medication)
            medication.medId = "MED789"
            assertEquals("MED789", medication.medId)

            val medicament = MedicamentDAO.findById(1)
            assertNotNull(medicament)
            medicament.medicamentId = "MEDIC003"
            assertEquals("MEDIC003", medicament.medicamentId)
        }
    }


    @Test
    @Order(4)
    fun `test Delete operations`() {
        transaction {

            val medicament = MedicamentDAO.findById(1)
            assertNotNull(medicament)
            medicament.delete()
            assertNull(MedicamentDAO.findById(1))

            val medication = MedicationDAO.findById(1)
            assertNotNull(medication)
            medication.delete()
            assertNull(MedicationDAO.findById(1))

            val patient = PatientDAO.findById(1)
            assertNotNull(patient)
            patient.delete()
            assertNull(PatientDAO.findById(1))
        }
    }

}