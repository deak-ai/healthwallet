package ch.healthwallet.db

import ch.healthwallet.data.chmed16a.MedicamentDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import ch.healthwallet.data.chmed16a.PatientIdDTO
import ch.healthwallet.refdata.Article
import ch.healthwallet.refdata.Item
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestPisDbRepository {

    private lateinit var pisDbRepo:PisDbRepository;
    private val patient1Initial = PatientDTO(
        "Jane",
        "Smith",
        "1985-05-01",
        2
    )

    private val patient1Final = PatientDTO(
        "Jane",
        "Doe",
        "1985-05-01",
        2,
        listOf(PatientIdDTO(2, "1"))
    )

    private val insuranceIdValue = "GSHV123456"
    private val patient2Initial = PatientDTO(
        "John",
        "Boe",
        "1981-02-02",
        1,
        listOf(PatientIdDTO(1, insuranceIdValue))
    )

    private val patient2Final = PatientDTO(
        "John",
        "Doe",
        "1981-02-02",
        1,
        listOf(PatientIdDTO(1, insuranceIdValue),
            PatientIdDTO(2,"2"))
    )

    private lateinit var patientId1:String
    private lateinit var patientId2:String
    private lateinit var medicationId: String

    private fun getEnvOrDefault(envVar: String, defaultValue: String): String {
        return System.getenv(envVar) ?: defaultValue
    }

    @BeforeAll
    fun setup() {
        pisDbRepo = PisDbRepositoryImpl()
        Database.connect(
            getEnvOrDefault("DB_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;"),
            driver = getEnvOrDefault("DB_DRIVER", "org.h2.Driver"),
            user = getEnvOrDefault("DB_USER", ""),
            password = getEnvOrDefault("DB_PASSWORD", "")
        )
        transaction {
            SchemaUtils.drop(*PisDbRepository.SSI_EMEDIPLAN_TABLES.toTypedArray())
            SchemaUtils.create(*PisDbRepository.SSI_EMEDIPLAN_TABLES.toTypedArray())
        }
    }

    @Test
    @Order(1)
    fun `load and check static data`() {
        initializeMedicamentsRefDataTable("src/main/resources/refdata/Articles_ALL_ALL_20240429170557.xml")

        transaction {
            MedicamentRefDataDAO.all().forEach {
                println("ID: ${it.id.value}, Atype: ${it.atype}, GTIN: ${it.gtin}, SWMC_AUTHNR: ${it.swmcAuthnr}, NAME_DE: ${it.nameDe}, NAME_FR: ${it.nameFr}, ATC: ${it.atc}, AUTH_HOLDER_NAME: ${it.authHolderName}, AUTH_HOLDER_GLN: ${it.authHolderGln}, Date: ${it.dt}")
            }
        }

    }



    private fun createAndCheck(patient: PatientDTO,
                               systemIdValue: String?,
                               insuranceIdValue: String?): PatientDTO {

        val newPatient = pisDbRepo.createOrUpdatePatient(patient)
        assertNotNull(newPatient)
        println(newPatient)
        assertNotNull(newPatient.ids)
        val systemId: PatientIdDTO? = newPatient.ids!!.find { it.type == 2 }
        assertNotNull(systemId)
        systemIdValue?.let {
            assertEquals(systemIdValue,systemId.value)
        }
        insuranceIdValue?.let {
            val insuranceId: PatientIdDTO? = newPatient.ids!!.find { it.type == 1 }
            assertNotNull(insuranceId)
            assertEquals(insuranceIdValue,insuranceId.value)
        }
        return newPatient
    }

    @Test
    @Order(2)
    fun `test Create patient1 without ids`() {
        val newPatient = createAndCheck(patient1Initial, null, null)
        patientId1 = newPatient.ids!!.find { it.type == 2 }!!.value
    }



    @Test
    @Order(3)
    fun `test Create patient2 with health insurance id`() {
        val newPatient = createAndCheck(patient2Initial, null, insuranceIdValue)
        patientId2 = newPatient.ids!!.find { it.type == 2 }!!.value
    }


    @Test
    @Order(4)
    fun `test Update patient1 without insurance id`() {
        // The last name of the patient is updated.
        val patient = patient1Initial.copy(lastName = "Doe",
            ids = listOf(PatientIdDTO(2, patientId1))
        )
        createAndCheck(patient, patientId1, null)
    }

    @Test
    @Order(5)
    fun `test Update patient2 with health insurance id`() {
        // The last name of the patient is updated.
        val patient = patient2Initial.copy(lastName = "Doe",
            ids = patient2Initial.ids?.plus(PatientIdDTO(2, patientId2))
        )
        createAndCheck(patient, patientId2, insuranceIdValue)
    }

    @Test
    @Order(6)
    fun `test Update non existing patient with invalid system id format fails`() {
        val patient = patient1Initial.copy(
            ids = listOf(PatientIdDTO(2, "ID12345"))
        )
       assertThrows<IllegalArgumentException> {
           pisDbRepo.createOrUpdatePatient(patient)
       }
    }

    @Test
    @Order(7)
    fun `test Update non existing patient with non-existing system id format fails`() {
        val patient = patient1Initial.copy(
            ids = listOf(PatientIdDTO(2, "9999"))
        )
        assertThrows<IllegalStateException> {
            pisDbRepo.createOrUpdatePatient(patient)
        }
    }

    @Test
    @Order(8)
    fun `test Get patient 1 by id`() {
        val patient = pisDbRepo.getPatient(1)
        assertNotNull(patient)
        println(patient)
        assertEquals(patient1Final, patient)
    }

    @Test
    @Order(9)
    fun `test Get patient 2 by id`() {
        val patient = pisDbRepo.getPatient(2)
        assertNotNull(patient)
        println(patient)
        assertEquals(patient2Final, patient)
    }


    @Test
    @Order(10)
    fun `test Get all patients`() {
        val patients = pisDbRepo.getPatients()
        assertNotNull(patients)
        println(patients)
        assertEquals(listOf(patient1Final, patient2Final), patients)
    }

    @Test
    @Order(11)
    fun `test Create medicament for existing patient1`() {
        // using existing patient
        val patient = patient1Initial.copy(lastName = "Doe",
            ids = listOf(PatientIdDTO(2, patientId1))
        )
        createAndCheckMedication(patient)
    }

    @Test
    @Order(12)
    fun `test Create medicament for new patient`() {
        // using existing patient
        val patient = PatientDTO("Max","Muster", "2000-01-01")
        createAndCheckMedication(patient)
    }

    private fun createAndCheckMedication(patient: PatientDTO) {
        val medicament = MedicamentDTO(
            medId = "7680501410985",
            medIdType = 2
        )

        val medication = MedicationDTO(
            patient = patient,
            medicaments = listOf(medicament),
            author = "GLN123456",
        )
        val jsonFormatter = Json { prettyPrint = true }
        println("Request:")
        println(jsonFormatter.encodeToString(MedicationDTO.serializer(), medication))

        val medicationDTO = pisDbRepo.createMedication(medication)
        assertNotNull(medicationDTO)
        val medId = medicationDTO.medId
        assertNotNull(medId)
        medicationId = medId
        println("Response:")
        println(medicationDTO)
        println(jsonFormatter.encodeToString(MedicationDTO.serializer(), medicationDTO))
    }


    @Test
    @Order(13)
    fun `test Get all medications`() {
        val medications = pisDbRepo.getMedications()
        assertNotNull(medications)
        assertFalse { medications.isEmpty() }
        println(medications)
    }

    @Test
    @Order(14)
    fun `test Get medication by internal id`() {
        val medication = pisDbRepo.getMedicationByInternalId(1)
        assertNotNull(medication)
        println(medication)
    }

    @Test
    @Order(15)
    fun `test Get medication by non-existing internal id fails`() {
        val medication = pisDbRepo.getMedicationByInternalId(99)
        assertNull(medication)
    }

    @Test
    @Order(16)
    fun `test Get medication by id`() {
        val medication = pisDbRepo.getMedicationById(medicationId)
        assertNotNull(medication)
        println(medication)
    }

    @Test
    @Order(17)
    fun `test Get medication by non existing id fails`() {
        val medication = pisDbRepo.getMedicationById("dummy")
        assertNull(medication)
    }

}