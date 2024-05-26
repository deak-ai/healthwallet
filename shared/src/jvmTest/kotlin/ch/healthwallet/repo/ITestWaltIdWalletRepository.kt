package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class ITestWaltIdWalletRepository {

    companion object {

        val waltIdPrefs = MutableStateFlow(WaltIdPrefs(
                waltIdEmail = "user@email.com",
                waltIdPassword = "password",
                waltIdWalletApi = "http://localhost:7001"
            ))

        val httpClient = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }

        }

        val repo: WaltIdWalletRepository = WaltIdWalletRepositoryImpl(
            httpClient, waltIdPrefs.asStateFlow()
        )
    }

    @Test
    fun `Calling login with valid credentials returns 200 and success result`() {
        runTest {
            val result = repo.login()
            assertTrue(result.isSuccess)
            val loginResponse = result.getOrNull()
            assertNotNull(loginResponse)
            assertEquals(waltIdPrefs.value.waltIdEmail, loginResponse.username)
            assertNotNull(loginResponse.token)
            assertNotNull(loginResponse.id)

        }
    }

    @Test
    fun `Calling authLogin with unknown user returns 400 and failure result`() = runTest {
        waltIdPrefs.value = waltIdPrefs.value.copy(waltIdEmail = "unknown@email.com")
        val result = repo.login()
        assertTrue(result.isFailure)
    }

    @Test
    fun `Calling authLogin with wrong endpoint returns with failure`() = runTest {
        waltIdPrefs.value = waltIdPrefs.value.copy(waltIdWalletApi = "http://localhost:1")
        val result = repo.login()
        assertTrue(result.isFailure)
        println(result.exceptionOrNull())
    }


    @Ignore // disabled to not create too many users
    @Test
    fun `Calling authCreate with new user returns 201 and succeeds`() = runTest {
        waltIdPrefs.value = waltIdPrefs.value.copy(waltIdEmail = UUID.randomUUID().toString())
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
        val result = repo.getWallets()
        assertTrue(result.isSuccess)
        println(result.getOrNull())
    }

    @Test
    fun `Calling getDids when logged in succeeds and returns list of DIDDetail`() = runTest {
        repo.login().onFailure {
            fail(it.message)
        }
        val (_, wallets) = repo.getWallets().getOrElse {
            fail(it.message)
        }
        val walletId = wallets.first().id
        val result = repo.getDids(walletId)
        assertTrue(result.isSuccess)
        println(result.getOrNull())
    }

    @Test
    fun `Calling queryCredentials returns parsed list of verified credentials`() = runTest {
        val login = repo.login()
        println(login.getOrThrow())
        val wallets: Result<WalletList> = repo.getWallets()
        val walletId = wallets.getOrElse {
            fail("No walletId found") }.wallets.first().id

        val result = repo.queryCredentials(CredentialsQuery(walletId = walletId))
        println(result.getOrNull())
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Calling getCredential with valid parameters returns verified credential`() = runTest {
        val login = repo.login()
        println(login.getOrThrow())
        val wallets: Result<WalletList> = repo.getWallets()
        val walletId = wallets.getOrElse {
            fail("No walletId found") }.wallets.first().id
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