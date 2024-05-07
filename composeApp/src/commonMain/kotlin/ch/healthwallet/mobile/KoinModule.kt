package ch.healthwallet.mobile

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ch.healthwallet.mobile.tabs.settings.LoginCredentialsScreen
import ch.healthwallet.mobile.tabs.settings.LoginCredentialsViewModel
import ch.healthwallet.mobile.tabs.settings.SettingsScreen

val koinModule = module {

    single {
        io.ktor.client.HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }
    }

    singleOf(::LoginCredentialsViewModel)
    singleOf(::LoginCredentialsScreen)
    singleOf(::SettingsScreen)

}