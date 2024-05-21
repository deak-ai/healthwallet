package ch.healthwallet.web

import SERVER_PORT
import ch.healthwallet.web.di.mainModule
import ch.healthwallet.web.plugins.configureOpenApi
import ch.healthwallet.web.plugins.configureRouting
import ch.healthwallet.web.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0") {
        install(Koin) {
            modules(mainModule)
            module()
        }
        Database.connect(
            "jdbc:postgresql://localhost:5432/healthssi_pis",
            driver = "org.postgresql.Driver",
            user = "pgupis",
            password = "**********"
        )
    }.start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
    configureOpenApi()
}