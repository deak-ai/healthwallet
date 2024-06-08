package ch.healthwallet.di

import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.repo.PisServerRepository
import ch.healthwallet.repo.PisServerRepositoryImpl
import ch.healthwallet.repo.WaltIdWalletRepository
import ch.healthwallet.repo.WaltIdWalletRepositoryImpl
import ch.healthwallet.tabs.home.HomeScreenModel
import ch.healthwallet.tabs.settings.WalletSettingsScreen
import ch.healthwallet.tabs.settings.WalletSettingsScreenModel
import ch.healthwallet.tabs.vc.VCScreenModel
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

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

    single<WaltIdWalletRepository> { WaltIdWalletRepositoryImpl(get(), get<AppPrefsRepository>().appPrefs) }
    //single<WaltIdIssuerRepository> { WaltIdIssuerRepositoryImpl(get(), get<AppPrefsRepository>().appPrefs) }
    single<PisServerRepository> { PisServerRepositoryImpl(get(), get<AppPrefsRepository>().appPrefs) }

    singleOf(::HomeScreenModel)
    singleOf(::WalletSettingsScreenModel)
    factoryOf(::WalletSettingsScreen)
    singleOf(::VCScreenModel)

}