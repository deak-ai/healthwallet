package ch.healthwallet.db

import ch.healthwallet.data.chmed16a.MedicamentDTO
import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.data.chmed16a.MedicationDTO
import ch.healthwallet.data.chmed16a.PatientDTO
import ch.healthwallet.data.chmed16a.PatientIdDTO
import kotlinx.datetime.format
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.time.format.DateTimeFormatter


object MedicationsTable : IntIdTable() {
    val patientId = reference("patient_id", PatientsTable)
    val medType = integer("med_type")
    val medId = varchar("med_id", 50).index()
    val author = varchar("author", 100)
    val zsrNumber = varchar("zsr_number", 10).nullable()
    val creationDate = varchar("creation_date", 25)
    val remarks = varchar("remarks", 255).nullable()
}

object PatientsTable : IntIdTable() {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val birthDate = varchar("birth_date", 10) // ISO 8601 date format
    val gender = integer("gender").nullable()
    init {
        uniqueIndex("unique_patient_index", firstName, lastName, birthDate)
    }
}

object PatientIdsTable : IntIdTable() {
    val patientId = reference("patient_id", PatientsTable)
    val idType = integer("id_type")
    val idValue = varchar("id_value", 100)
}

object MedicamentsTable : IntIdTable() {
    val medicationId = reference("medication_id", MedicationsTable)
    val medId = varchar("med_id", 128)
    val medIdType = integer("med_id_type")
}

object MedicamentsRefDataTable : IntIdTable() {
    val dt = datetime("dt")
    val atype = varchar("atype", 50)
    val gtin = varchar("gtin", 20)
    val swmcAuthnr = varchar("swmc_authnr", 20)
    val nameDe = varchar("name_de", 255)
    val nameFr = varchar("name_fr", 255)
    val atc = varchar("atc", 10).nullable()
    val authHolderName = varchar("auth_holder_name", 255)
    val authHolderGln = varchar("auth_holder_gln", 20).nullable()
}


class MedicationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicationDAO>(MedicationsTable)
    var medType by MedicationsTable.medType
    var medId by MedicationsTable.medId
    var author by MedicationsTable.author
    var zsrNumber by MedicationsTable.zsrNumber
    var creationDate by MedicationsTable.creationDate
    var remarks by MedicationsTable.remarks
    val medicaments by MedicamentDAO referrersOn MedicamentsTable.medicationId
    var patient by PatientDAO referencedOn MedicationsTable.patientId
}

fun MedicationDAO.toDTO(): MedicationDTO = MedicationDTO(
    medType = medType,
    medId = medId,
    author = author,
    zsrNumber = zsrNumber,
    creationDate = creationDate,
    remarks = remarks,
    medicaments = medicaments.map { it.toDTO() },
    patient = patient.toDTO()
)



class PatientDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PatientDAO>(PatientsTable)
    var firstName by PatientsTable.firstName
    var lastName by PatientsTable.lastName
    var birthDate by PatientsTable.birthDate
    var gender by PatientsTable.gender
    val medications by MedicationDAO referrersOn MedicationsTable.patientId
    val ids by PatientIdDAO referrersOn PatientIdsTable.patientId
}


fun PatientDAO.toDTO(): PatientDTO {
    return PatientDTO(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        gender = gender,
        ids = ids.map { it.toDTO() }
    )
}

class PatientIdDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PatientIdDAO>(PatientIdsTable)
    var patient by PatientDAO referencedOn PatientIdsTable.patientId
    var idType by PatientIdsTable.idType
    var idValue by PatientIdsTable.idValue
}


fun PatientIdDAO.toDTO(): PatientIdDTO = PatientIdDTO(
    type = idType,
    value = idValue
)

class MedicamentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicamentDAO>(MedicamentsTable)
    var medId by MedicamentsTable.medId
    var medIdType by MedicamentsTable.medIdType
    var medication by MedicationDAO referencedOn MedicamentsTable.medicationId
}

fun MedicamentDAO.toDTO(): MedicamentDTO = MedicamentDTO(
    medId = medId,
    medIdType = medIdType
)

class MedicamentRefDataDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicamentRefDataDAO>(MedicamentsRefDataTable)

    var dt by MedicamentsRefDataTable.dt
    var atype by MedicamentsRefDataTable.atype
    var gtin by MedicamentsRefDataTable.gtin
    var swmcAuthnr by MedicamentsRefDataTable.swmcAuthnr
    var nameDe by MedicamentsRefDataTable.nameDe
    var nameFr by MedicamentsRefDataTable.nameFr
    var atc by MedicamentsRefDataTable.atc
    var authHolderName by MedicamentsRefDataTable.authHolderName
    var authHolderGln by MedicamentsRefDataTable.authHolderGln

}

fun MedicamentRefDataDAO.toDTO(): MedicamentRefDataDTO {
    return MedicamentRefDataDTO(
        atype = this.atype,
        gtin = this.gtin,
        swmcAuthnr = this.swmcAuthnr,
        nameDe = this.nameDe,
        nameFr = this.nameFr,
        atc = this.atc,
        authHolderName = this.authHolderName,
        authHolderGln = this.authHolderGln,
        date = this.dt.toJavaLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    )
}
