package ch.healthwallet.refdata

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("ARTICLE", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
data class Article(

    @XmlSerialName("CREATION_DATETIME", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @Serializable(with = InstantSerializer::class)
    val creationDateTime: Instant,

    @XmlElement(true) val items: List<Item>,

    @XmlElement(true) val result: Result
)

@Serializable
@XmlSerialName("ITEM", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
data class Item(
    @XmlSerialName("ATYPE", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val atype: String,

    @XmlSerialName("GTIN", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val gtin: String,

    @XmlSerialName("SWMC_AUTHNR", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val swmcAuthnr: String,

    @XmlSerialName("NAME_DE", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val nameDe: String,

    @XmlSerialName("NAME_FR", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val nameFr: String,

    @XmlSerialName("ATC", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val atc: String? = null,

    @XmlSerialName("AUTH_HOLDER_NAME", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val authHolderName: String,

    @XmlSerialName("AUTH_HOLDER_GLN", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val authHolderGln: String? = null,

    @XmlSerialName("DT", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime
)

@Serializable
@XmlSerialName("RESULT", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
data class Result(
    @XmlSerialName("OK_ERROR", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val okError: String,

    @XmlSerialName("NBR_RECORD", namespace = "http://refdatabase.refdata.ch/Article_out", prefix = "")
    @XmlElement(true)
    val nbrRecord: Int
)

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return decoder.decodeString().toInstant()
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val timeZone = TimeZone.of("Europe/Zurich") // Specify your desired time zone here

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val zonedDateTime = value.toInstant(timeZone).toLocalDateTime(timeZone)
        encoder.encodeString(zonedDateTime.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val dateTimeString = decoder.decodeString()
        val parsedDateTime = LocalDateTime.parse(dateTimeString)
        val zonedDateTime = parsedDateTime.toInstant(timeZone).toLocalDateTime(timeZone)
        return zonedDateTime
    }
}