package ch.healthwallet.web.di

import ch.healthwallet.db.PisDbRepository
import ch.healthwallet.db.PisDbRepositoryImpl
import ch.healthwallet.repo.WaltIdIssuerRepository
import ch.healthwallet.repo.WaltIdIssuerRepositoryImpl
import ch.healthwallet.repo.AppPrefs
import ch.healthwallet.repo.WaltIdVerifierRepository
import ch.healthwallet.repo.WaltIdVerifierRepositoryImpl
import ch.healthwallet.vc.PrescriptionVCIssuanceService
import ch.healthwallet.vc.PrescriptionVCIssuanceServiceImpl
import ch.healthwallet.vp.VerifiablePresentationManager
import ch.healthwallet.vp.VerifiablePresentationManagerImpl
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
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

    // issuer repo
    val issuerBaseUrl = config.property("services.issuer").getString()
    val appPrefsIssuer = MutableStateFlow(AppPrefs(waltIdIssuerApi = issuerBaseUrl)).asStateFlow()
    single<WaltIdIssuerRepository> {
        WaltIdIssuerRepositoryImpl(get(), appPrefsIssuer)
    }

    // issuer service
    single<PrescriptionVCIssuanceService> {
        PrescriptionVCIssuanceServiceImpl(get(), get()) }

    // verifier repo
    val verifierBaseUrl = config.property("services.verifier").getString()
    val appPrefsVerifier = MutableStateFlow(AppPrefs(waltIdVerifierApi = verifierBaseUrl)).asStateFlow()
    single<WaltIdVerifierRepository> {
        WaltIdVerifierRepositoryImpl(get(), appPrefsVerifier)
    }

    single<VerifiablePresentationManager> {
        VerifiablePresentationManagerImpl(get())
    }
}