package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ITestPisServerRepository {

    companion object {

        private val httpClient = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }
        }

        val pisSeverRepo: PisServerRepository = PisServerRepositoryImpl(
            httpClient, MutableStateFlow(ITestWaltIdWalletRepository.appPrefs.value.
            copy()
        ))

    }

    @Test
    fun `find medicament refdata with valid gtin returns medicament`() {
        runTest {
            val gtin = "7680332730610"
            val result =
                pisSeverRepo.findMedicamentRefDataByGTIN(gtin)

            val med = result.getOrElse {
                fail("No medicament found")
            }
            assertEquals(gtin, med.gtin)
            println(med.nameDe)

        }
    }

    @Test
    fun `find medicament refdata with invalid gtin returns error`() {
        runTest {
            val gtin = "invalidGtin"
            val result =
                pisSeverRepo.findMedicamentRefDataByGTIN(gtin)

            assertTrue { result.isFailure }

            result.onFailure {
                assertEquals(HttpStatusCode.NotFound,
                    (it as ClientRequestException).response.status)
            }

        }
    }


}