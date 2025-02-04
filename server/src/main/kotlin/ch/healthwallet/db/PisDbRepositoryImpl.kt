package ch.healthwallet.db

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import ch.healthwallet.data.chmed16a.PatientIdDTO
import ch.healthwallet.util.createTimestampWithTimeZone
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class PisDbRepositoryImpl() :PisDbRepository {

    private val timeZone = "Europe/Zurich"

    init {
        println("init PisDbRepositoryImpl")
    }


    override fun createOrUpdatePatient(patient: PatientDTO): PatientDTO {
        return transaction {
            val patientDAO = getOrCreatePatientDAO(patient)
            patientDAO.toDTO()
        }
    }

    private fun getOrCreatePatientDAO(patient: PatientDTO): PatientDAO {
        // Try to extract and parse the system ID from the provided IDs
        val systemId = patient.ids?.find { it.type == 2 }?.value?.toIntOrNull()

        if (systemId == null && patient.ids?.any { it.type == 2 } == true) {
            throw IllegalArgumentException("Invalid system ID provided")
        }

        val patientDAO = systemId?.let {
            // Attempt to find the patient by system ID
            PatientDAO.findById(it) ?: throw IllegalStateException("No patient found with system ID $it")
        } ?: PatientDAO.new { }

        patientDAO.apply {
            firstName = patient.firstName
            lastName = patient.lastName
            birthDate = patient.birthDate
            gender = patient.gender
        }

        // Manage IDs including system IDs (type = 2)
        managePatientIds(patientDAO, patient.ids)
        return patientDAO
    }

    private fun managePatientIds(patient: PatientDAO, ids: List<PatientIdDTO>?) {
        // Check if any IDs are provided
        if (!ids.isNullOrEmpty()) {
            // Process each ID provided in the DTO
            ids.forEach { idDto ->
                val existingId = patient.ids.find { it.idType == idDto.type }
                if (existingId == null) {
                    PatientIdDAO.new {
                        this.patient = patient
                        idType = idDto.type
                        idValue = idDto.value
                    }
                } else {
                    existingId.idValue = idDto.value
                    existingId.flush()
                }
            }
        }

        // Check for existence of a system ID, add if missing
        val hasSystemId = patient.ids.any { it.idType == 2 }
        if (!hasSystemId) {
            PatientIdDAO.new {
                this.patient = patient
                idType = 2
                idValue = patient.id.value.toString()
            }
        }
    }

    override fun getPatients(): List<PatientDTO> {
       return transaction {
           val patients = PatientDAO.all().map { it.toDTO() }
           patients
       }
    }

    override fun getPatient(patientId: Int): PatientDTO? {
        return transaction {
            val patient = PatientDAO.findById(patientId)
            patient?.toDTO()
        }
    }

    override fun createMedication(medication: MedicationDTO): MedicationDTO {
        return transaction {
            val patientDAO = getOrCreatePatientDAO(medication.patient!!)
            val medicationDAO = MedicationDAO.new {
                patient = patientDAO
                medType = medication.medType
                medId = UUID.randomUUID().toString()
                author = medication.author
                zsrNumber = medication.zsrNumber
                creationDate = createTimestampWithTimeZone(timeZone)
                remarks = medication.remarks
            }
            medication.medicaments.forEach { medicamentDTO ->
                MedicamentDAO.new {
                    this.medication = medicationDAO
                    this.medId = medicamentDTO.medId
                    this.medIdType = medicamentDTO.medIdType
                }
            }
            medicationDAO.toDTO()
        }

    }

    override fun getMedications(): List<MedicationDTO> {
        return transaction {
            val medications = MedicationDAO.all().map { it.toDTO() }
            medications
        }
    }

    override fun getMedicationByInternalId(medicationId: Int): MedicationDTO? {
        return transaction {
            val medication = MedicationDAO.findById(medicationId)
            medication?.toDTO()
        }
    }

    override fun getMedicationById(medicationId: String): MedicationDTO? {
        return transaction {
            val medication: MedicationDAO? =
                MedicationDAO.find { MedicationsTable.medId eq medicationId }.singleOrNull()
            medication?.toDTO()
        }
    }

    override fun findMedicamentRefDataBySubstring(substring: String): List<MedicamentRefDataDTO> {
        return transaction {
            MedicamentRefDataDAO.find { MedicamentsRefDataTable.nameDe.lowerCase() like "%${substring.lowercase()}%" }
                .map { it.toDTO() }
        }
    }

    override fun findMedicamentRefDataByGTIN(gtin: String): MedicamentRefDataDTO? {
        return transaction {
            MedicamentRefDataDAO.find { MedicamentsRefDataTable.gtin eq gtin }
                .map { it.toDTO() }
                .singleOrNull()
        }
    }

}