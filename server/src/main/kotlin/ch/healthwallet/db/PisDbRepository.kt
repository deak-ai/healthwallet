package ch.healthwallet.db

import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO

interface PisDbRepository {

    fun createOrUpdatePatient(patient: PatientDTO): PatientDTO

    fun getPatients(): List<PatientDTO>

    fun getPatient(patientId: Int): PatientDTO?

    fun createMedication(medication: MedicationDTO): MedicationDTO

    fun getMedications(): List<MedicationDTO>

    fun getMedicationByInternalId(medicationId: Int): MedicationDTO?

    fun getMedicationById(medicationId: String): MedicationDTO?

}