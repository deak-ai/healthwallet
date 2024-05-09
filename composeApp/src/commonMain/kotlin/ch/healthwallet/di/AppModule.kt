package ch.healthwallet.di

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module
import ch.healthwallet.tabs.settings.WalletSettingsScreen
import ch.healthwallet.tabs.settings.WalletSettingsViewModel
import ch.healthwallet.tabs.settings.SettingsTabScreen
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf

val appModule = module {

    single {
        io.ktor.client.HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpCookies)
        }
    }

    factoryOf(::WalletSettingsViewModel)
    factoryOf(::WalletSettingsScreen)
    factoryOf(::SettingsTabScreen)

}