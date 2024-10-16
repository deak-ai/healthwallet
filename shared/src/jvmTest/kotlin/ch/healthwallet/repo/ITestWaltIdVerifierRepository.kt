package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class ITestWaltIdVerifierRepository {

    companion object {
        val repo: WaltIdVerifierRepository = WaltIdVerifierRepositoryImpl(
            createHttpClient(),
            MutableStateFlow(
                AppPrefs()).asStateFlow()
        )

        @OptIn(ExperimentalSerializationApi::class)
        private fun createHttpClient() = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }

    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        //explicitNulls = false
    }

    @Test
    fun `Calling Verifier API with Presentation definition returns valid OIDC4VP Url`() {
        runTest {
            val verifyRequest = VerifyRequest(statusCallbackUri = "https://pis.healthwallet.li/vp/status")
            println(json.encodeToString(VerifyRequest.serializer(), verifyRequest))
            val result = repo.verify(verifyRequest)

            val url = result.getOrElse {
                fail("No Url in result, $it")
            }
            printUrl(url)
        }
    }

    private fun printUrl(url: Url) {
        println(url)
        val reqParams = url.parameters.toMap()
        println(url.protocol.name)
        println(url.host)
        println(reqParams)
    }

    @Test
    fun `Calling Verifier API with invalid request returns 400 error`() {
        runTest {
            val result = repo.verify(VerifyRequest(responseMode = "unknown"))
            val error = result.exceptionOrNull() as ClientRequestException
            assertNotNull(error)
            assertEquals(error.response.status.value, 400)
            println(error)
        }
    }


}