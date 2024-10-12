package ch.healthwallet.repo

import ch.healthwallet.util.decodeJwtPayload
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE

import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.Result
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

import saschpe.kase64.*


class ITestWaltIdWalletRepository {

    companion object {

        val appPrefs = MutableStateFlow(AppPrefs(
                waltIdEmail = "user@email.com",
                waltIdPassword = "password"
            ))

        private val httpClient = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL // Log everything (headers, body, etc.)
                logger = Logger.SIMPLE
            }
        }

        val repo: WaltIdWalletRepository = WaltIdWalletRepositoryImpl(
            httpClient, appPrefs.asStateFlow()
        )
    }

    @Test
    fun `Calling login with valid credentials returns 200 and success result`() {
        runTest {
            val result = repo.login()
            assertTrue(result.isSuccess)
            val loginResponse = result.getOrNull()
            assertNotNull(loginResponse)
            assertEquals(appPrefs.value.waltIdEmail, loginResponse.username)
            assertNotNull(loginResponse.token)
            assertNotNull(loginResponse.id)

        }
    }

    @Test
    fun `Calling authLogin with unknown user returns 400 and failure result`() = runTest {
        appPrefs.value = appPrefs.value.copy(waltIdEmail = "unknown@email.com")
        val result = repo.login()
        assertTrue(result.isFailure)
    }

    @Test
    fun `Calling authLogin with wrong endpoint returns with failure`() = runTest {
        appPrefs.value = appPrefs.value.copy(waltIdWalletApi = "http://localhost:1")
        val result = repo.login()
        assertTrue(result.isFailure)
        println(result.exceptionOrNull())
    }


    @Ignore // disabled to not create too many users
    @Test
    fun `Calling authCreate with new user returns 201 and succeeds`() = runTest {
        appPrefs.value = appPrefs.value.copy(waltIdEmail = UUID.randomUUID().toString())
        val result = repo.createUser()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Calling authCreate with existing user fails with 500`() = runTest {
        val result = repo.createUser()
        assertTrue(result.isFailure)
        println(result.exceptionOrNull())
    }

    @Ignore // disabled when running all tests as authLogin sets cookie
    @Test
    fun `Calling getUserId when not logged in fails`() = runTest {
        val result = repo.getUserId()
        assertTrue(result.isFailure)
        println(result.exceptionOrNull())
    }

    @Test
    fun `Calling getUserId when logged in succeeds`() = runTest {
        val result = repo.getUserId()
        assertTrue(result.isSuccess)
        println(result.getOrNull())
    }

    @Test
    fun `Calling Logout always succeeds`() = runTest {
        val result = repo.logout()
        assertTrue {
            result.getOrElse {
                println(it)
                false
            }
        }
    }

    @Test
    fun `Calling getWallets when logged in succeeds and returns wallet list`() = runTest {
        //repo.login()
        val result = repo.getWallets()
        assertTrue(result.isSuccess)
        println(result.getOrNull())
    }

    @Test
    fun `Calling getDids when logged in succeeds and returns list of DIDDetail`() = runTest {
        repo.login()
        val walletId = getWalletId()
        val result = repo.getDids(walletId)
        assertTrue(result.isSuccess)
        println(result.getOrNull())
    }

    @Test
    fun `Calling queryCredentials returns parsed list of verified credentials`() = runTest {
        repo.login()
        val walletId = getWalletId()

        val result = repo.queryCredentials(CredentialsQuery(walletId = walletId))
        val map: List<JsonObject> = result.getOrThrow().map { vc ->
            decodeJwtPayload(vc.document)
        }
        println(map.map {
            Json.encodeToString(it)
        })



    }


    @Test
    fun `Calling getCredential with valid parameters returns verified credential`() {
        runTest {
            val walletId = getWalletId()
            val credentials = repo.queryCredentials(CredentialsQuery(walletId = walletId))
                .getOrElse { fail("Unable to find credentials") }

            if (credentials.isNotEmpty()) {
                val firstCredential = credentials.first()
                val credentialId = firstCredential.credentialId

                val result = repo.getCredential(CredentialRequest(walletId, credentialId))
                assertTrue(result.isSuccess)
                val queriedCredential = result.getOrElse {
                    fail("No verified credential result")
                }
                assertEquals(walletId, queriedCredential.walletId)
                assertEquals(credentialId, queriedCredential.credentialId)
                assertEquals(firstCredential.addedOn, queriedCredential.addedOn)
            }
        }
    }

    private suspend fun getWalletId(): String {
        val login = repo.login()
        println(login.getOrThrow())
        val wallets: Result<WalletList> = repo.getWallets()
        val walletId = wallets.getOrElse {
            fail("No walletId found")
        }.wallets.first().id
        return walletId
    }

    @Ignore
    @Test
    fun `Resolving presentation and matching definition returns list of credentials and can be used`() {
        runTest {
            val request = "openid4vp://authorize?response_type=vp_token&client_id=&response_mode=direct_post&state=tPECMRcJkVdO&presentation_definition_uri=http%3A%2F%2Fverifier-api%3A7003%2Fopenid4vc%2Fpd%2FtPECMRcJkVdO&client_id_scheme=redirect_uri&response_uri=http%3A%2F%2Fverifier-api%3A7003%2Fopenid4vc%2Fverify%2FtPECMRcJkVdO"
            val walletId = getWalletId()
            val result = repo.resolvePresentationRequest(walletId, Url(request))
            val url = result.getOrElse {
                fail("Failed to resolve presentation: $it")
            }
            printUrl(url)
            val pd = url.parameters.get("presentation_definition")
                ?:fail("No presentation definition")
            val pf = Json.decodeFromString<PresentationFilter>(pd)
            val vcResult = repo.matchCredentials(walletId, pf)
            val credentials = vcResult.getOrElse {
                fail("Failed to match credentials: $it")
            }
            println(credentials)

            val useRequest = UsePresentationRequest(
                presentationRequest = url.toString(),
                selectedCredentials = listOf(credentials.first().credentialId)
            )
            val useResult = repo.usePresentationRequest(walletId, useRequest)
            val claimedCredentials = useResult.getOrElse {
                fail("Failed to use presentation: $it")
            }
            println(claimedCredentials)

        }
    }


    @Test
    fun `Serializing PresentationFilter returns non-empty string`() {
        val pf = PresentationFilter()
        val json = pf.serialize()
        assertTrue(json.contains(AppPrefs.DEFAULT_VC_NAME))
    }


    private fun printUrl(url: Url) {
        println(url)
        val reqParams = url.parameters.toMap()
        println(url.protocol.name)
        println(url.host)
        println(reqParams)
    }

}