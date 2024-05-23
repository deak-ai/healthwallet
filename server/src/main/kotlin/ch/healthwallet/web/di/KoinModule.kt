package ch.healthwallet.web.di

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.db.PisDbRepositoryImpl
import ch.healthwallet.repo.WaltIdIssuerRepository
import ch.healthwallet.repo.WaltIdIssuerRepositoryImpl
import ch.healthwallet.vc.PrescriptionVCIssuanceService
import ch.healthwallet.vc.PrescriptionVCIssuanceServiceImpl
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun koinModule(config: ApplicationConfig) = module {

    single<PisDbRepository> { PisDbRepositoryImpl() }

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

    val issuerBaseUrl = config.property("services.issuer").getString()

    single<WaltIdIssuerRepository> {
        WaltIdIssuerRepositoryImpl(get(), issuerBaseUrl)
    }

    single<PrescriptionVCIssuanceService> {
        PrescriptionVCIssuanceServiceImpl(get(), get()) }

}