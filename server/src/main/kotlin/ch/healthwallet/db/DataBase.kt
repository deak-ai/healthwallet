package ch.healthwallet.db

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDb() {
    Database.connect(
        environment.config.property("db.url").getString(),
        driver = environment.config.property("db.driver").getString(),
        user = environment.config.property("db.user").getString(),
        password = environment.config.property("db.password").getString()
    )
    transaction {
        SchemaUtils.create(*PisDbRepository.SSI_EMEDIPLAN_TABLES.toTypedArray())
    }
}