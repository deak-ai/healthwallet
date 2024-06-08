package ch.healthwallet.db

import ch.healthwallet.refdata.Article
import io.ktor.server.application.*
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Application.configureDb() {
    val dbUrl = environment.config.property("db.url").getString()
    Database.connect(
        dbUrl,
        driver = environment.config.property("db.driver").getString(),
        user = environment.config.property("db.user").getString(),
        password = environment.config.property("db.password").getString()
    )
    transaction {
        SchemaUtils.create(*PisDbRepository.SSI_EMEDIPLAN_TABLES.toTypedArray())
    }

    if (!isDatabaseInitialized()) {
        initializeMedicamentsRefDataTable(
            environment.config.property("db.medrefdata").getString())
    }


}

fun isDatabaseInitialized(): Boolean {
    return transaction {
        MedicamentsRefDataTable.selectAll().count() > 1000
    }
}

fun initializeMedicamentsRefDataTable(xmlFileName: String) {
    val fileContent =
        File(xmlFileName).readText()
    val article: Article = XML.decodeFromString(Article.serializer(), fileContent)

    val batchSize = 200

    article.items.chunked(batchSize).forEach { chunk ->
        transaction {
            chunk.forEach { item ->
                MedicamentRefDataDAO.new {
                    dt = item.date
                    atype = item.atype
                    gtin = item.gtin
                    swmcAuthnr = item.swmcAuthnr
                    nameDe = item.nameDe
                    nameFr = item.nameFr
                    atc = item.atc
                    authHolderName = item.authHolderName
                    authHolderGln = item.authHolderGln
                }
            }
        }
    }
}