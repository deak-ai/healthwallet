
import ch.healthwallet.data.chmed16a.MedicamentDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object MedicationsTable : IntIdTable() {
    val patientId = reference("patient_id", PatientsTable)
    val medType = integer("med_type")
    val medId = varchar("med_id", 100)
    val author = varchar("author", 100)
    val zsrNumber = varchar("zsr_number", 10).nullable()
    val creationDate = varchar("creation_date", 10)
    val remarks = varchar("remarks", 255).nullable()
}

class MedicationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicationDAO>(MedicationsTable)

    var patient by PatientDAO referencedOn MedicationsTable.patientId
    val medicaments by MedicamentDAO referrersOn MedicamentsTable.medicationId

    var medType by MedicationsTable.medType
    var medId by MedicationsTable.medId
    var author by MedicationsTable.author
    var zsrNumber by MedicationsTable.zsrNumber
    var creationDate by MedicationsTable.creationDate
    var remarks by MedicationsTable.remarks
}

fun MedicationDAO.toDTO(): MedicationDTO = MedicationDTO(
    patient = patient.toDTO(),
    medicaments = medicaments.map { it.toDTO() },
    medType = medType,
    medId = medId,
    author = author,
    zsrNumber = zsrNumber,
    creationDate = creationDate,
    remarks = remarks
)


object PatientsTable : IntIdTable() {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val birthDate = varchar("birth_date", 10) // ISO 8601 date format
    val gender = integer("gender").nullable()
}

class PatientDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PatientDAO>(PatientsTable)
    var firstName by PatientsTable.firstName
    var lastName by PatientsTable.lastName
    var birthDate by PatientsTable.birthDate
    var gender by PatientsTable.gender
    val medications by MedicationDAO referrersOn MedicationsTable.patientId
}

// Conversion function to transform an Entity to a DTO
fun PatientDAO.toDTO() = PatientDTO(
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate,
    gender = gender
)


object MedicamentsTable : IntIdTable() {
    val medicationId = reference("medication_id", MedicationsTable)
    val medicamentId = varchar("medicament_id", 128)
    val medicamentIdType = integer("medicament_id_type")
}

class MedicamentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicamentDAO>(MedicamentsTable)
    var medicamentId by MedicamentsTable.medicamentId
    var medicamentIdType by MedicamentsTable.medicamentIdType
    var medication by MedicationDAO referencedOn MedicamentsTable.medicationId
}

fun MedicamentDAO.toDTO(): MedicamentDTO = MedicamentDTO(
    medicamentId = medicamentId,
    medicamentIdType = medicamentIdType
)


