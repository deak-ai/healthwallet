package ai.deak.shw.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
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
        val repo:WaltIdWalletRepository = WaltIdWalletRepositoryImpl(
            createHttpClient(),
            "http://localhost:7001"
        )

        private fun createHttpClient() = HttpClient() {
            install(HttpCookies)
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })
            }

        }
        val loginRequest = LoginRequest(type="email", email = "user@email.com", password = "password")
    }

    @Test
    fun `Calling authLogin with valid credentials returns 200 and success result`() {
        runTest {
            val result = repo.login(loginRequest)
            assertTrue(result.isSuccess)
            val loginResponse = result.getOrNull()
            assertNotNull(loginResponse)
            assertEquals(loginRequest.email, loginResponse.username)
            assertNotNull(loginResponse.token)
            assertNotNull(loginResponse.id)

        }
    }

    @Test
    fun `Calling authLogin with unknown user returns 400 and failure result`() = runTest {
        val result =
            repo.login(
                LoginRequest(
                    type = "email",
                    email = "unknown@email.com",
                    password = "password")
            )
        assertTrue(result.isFailure)
    }

    @Test
    fun `Calling authLogin with wrong endpoint returns with failure`() = runTest {
        val repo = WaltIdWalletRepositoryImpl(createHttpClient(), "http://localhost:1")
        val result =
            repo.login(
                LoginRequest(
                    type = "email",
                    email = "unknown@email.com",
                    password = "password")
            )
        assertTrue(result.isFailure)
        println(result.exceptionOrNull())
    }


    @Ignore // disabled to not create too many users
    @Test
    fun `Calling authCreate with new user returns 201 and succeeds`() = runTest {
        val randomUUID = UUID.randomUUID().toString()
        val result = repo.createUser(
            CreateUserRequest(
                name = "Test User",
                email = "$randomUUID@email.com",
                password = "password"
            )
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Calling authCreate with existing user fails with 500`() = runTest {
        val result = repo.createUser(
            CreateUserRequest(
                name = "Test User",
                email = "user@email.com",
                password = "password"
            )
        )
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
    fun `Calling queryCredentials returns parsed list of verified credentials`() = runTest {
        val login = repo.login(loginRequest)
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
        val login = repo.login(loginRequest)
        println(login.getOrThrow())
        val wallets: Result<WalletList> = repo.getWallets()
        val walletId = wallets.getOrElse {
            fail("No walletId found") }.wallets.first().id
        val firstCredential = repo.queryCredentials(CredentialsQuery(walletId = walletId))
            .getOrElse { fail("Unable to find credentials") }.first()
        val credentialId = firstCredential.credentialId

        val result = repo.getCredential(CredentialRequest(walletId, credentialId))
        assertTrue(result.isSuccess)
        val queriedCredential = result.getOrElse {
            fail("No verified credential result")
        }
        assertEquals(walletId, queriedCredential.walletId)
        assertEquals(credentialId, queriedCredential.credentialId)
        assertEquals(firstCredential.addedOn,queriedCredential.addedOn )
    }

}