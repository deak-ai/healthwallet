package ch.healthwallet.web

import ch.healthwallet.db.configureDb
import ch.healthwallet.web.di.koinModule
import ch.healthwallet.web.plugins.configureOpenApi
import ch.healthwallet.web.plugins.configureRouting
import ch.healthwallet.web.plugins.configureSerialization
import ch.healthwallet.web.plugins.configureWebsockets
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


fun Application.module() {

    install(Koin) {
        modules(koinModule(environment.config))
    }

    configureDb()

    configureRouting()

    configureWebsockets()

    configureSerialization()

    configureOpenApi()
}

