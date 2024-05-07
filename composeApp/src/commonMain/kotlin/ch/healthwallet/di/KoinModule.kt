package ch.healthwallet.di

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ch.healthwallet.tabs.settings.LoginCredentialsScreen
import ch.healthwallet.tabs.settings.LoginCredentialsViewModel
import ch.healthwallet.tabs.settings.SettingsScreen
import org.koin.core.module.dsl.factoryOf

val koinModule = module {

    single {
        io.ktor.client.HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }
    }


    factoryOf(::LoginCredentialsViewModel)
    factoryOf(::LoginCredentialsScreen)
    factoryOf(::SettingsScreen)

}