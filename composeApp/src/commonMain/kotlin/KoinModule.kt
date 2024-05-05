import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tabs.settings.LoginCredentialsScreen
import tabs.settings.LoginCredentialsViewModel
import tabs.settings.SettingsScreen

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