package ch.healthwallet.data.chmed23a
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*


/**
 * Partial test implementation of the 2024 CH EMED spec: https://emediplan.ch/chmed23a-wird-im-august-publiziert/
 * direct link to spec: https://emediplan.ch/wp-content/uploads/2023/09/20230815_eMediplan_ChMed23A_1.0-AND-eMediplan_ChMed23A_Posology_1.0.pdf
 */


@Serializable
data class Medication(
    val patient: Patient,
    val hcPatient: HealthcarePerson,
    val meds : List<Medicament>,
    val medType : Int, // 1: MedicationPlan (MP), 2: PolymedicationCheck (PMC) [deprecated], 3: Prescription(Rx)
    val id : String,
    val auth : Int) { // 1: Healthcare person, 2: Patient
    
    companion object {
        fun createPrescription(id: String, p: Patient, h: HealthcarePerson, m: Medicament): Medication {
            return Medication(p, h, listOf(m), 3, id, 1)
        }
    }
}



@Serializable
data class Patient(
    val id: String, // patient id
    val fName: String,        // First name
    val lName: String,        // Last name
    val bdt: String,          // Date of birth, format: yyyy-mm-dd (ISO 8601 Date)
    @Serializable(with = GenderSerializer::class)
    val gender: Gender        // Gender of the patient using enum
//val ids: List<PatientId>  // List of patient identifiers
)


@Serializable
enum class Gender(val id: Int) {
    Male(1),
    Female(2),
    Other(3)
}

object GenderSerializer : KSerializer<Gender> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Gender", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Gender) {
        encoder.encodeInt(value.id)
    }

    override fun deserialize(decoder: Decoder): Gender {
        val id = decoder.decodeInt()
        return Gender.values().first { it.id == id }
    }
}


//data class PatientId(
//    type
//)

@Serializable
data class HealthcarePerson(
    val gln: String,
    val fName: String,
    val lName: String,
    val street:String,
    val zip: String,
    val city: String)

@Serializable
data class Medicament(
    val id: String,
    val idType: Int)



