package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertNotNull

class ITestCredentialOfferFlow {

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

        val walletRepo: WaltIdWalletRepository = WaltIdWalletRepositoryImpl(
            httpClient,
            "https://wallet.healthwallet.li"
        )

        val issuerRepo: WaltIdIssuerRepository = WaltIdIssuerRepositoryImpl(
            httpClient,
            "https://issuer.healthwallet.li"
        )

        val loginRequest = ITestWaltIdWalletRepository.loginRequest
        val issueRequest = ITestWaltIdIssuerRepository.issueRequest

    }

    @Test
    fun `Issuing VC then importing as pending and then accepting works`() {
        runTest {

            val pending = true

            val (walletId, credentialId) = issueAndImportVc(pending)

            // accept credential
            walletRepo.acceptCredential(CredentialRequest(walletId, credentialId)).onFailure {
                fail(it.message)
            }

            // check credential was imported
            val credential = walletRepo.getCredential(
                CredentialRequest(walletId, credentialId)).
                    getOrElse {
                        fail(it.message)
                    }
            assertNotNull(credential)
            println(credential)

        }
    }

    @Test
    fun `Issuing VC then importing as pending and then rejecting works`() {
        runTest {

            val pending = false

            val (walletId, credentialId) = issueAndImportVc(pending)

            // reject credential
            walletRepo.rejectCredential(
                CredentialRequest(walletId, credentialId),
                "test reject"
            ).onFailure {
                fail(it.message)
            }

            // check credential is not available
            walletRepo.getCredential(
                CredentialRequest(walletId, credentialId)
            ).exceptionOrNull()?.let {
                assertNotNull(it)
            }
        }
    }

    private suspend fun issueAndImportVc(pending: Boolean): Pair<String, String> {
        // login and get default DID
        val walletId = login()
        val did = getDefaultDid(walletId)

        // issue using OID4VCI flow (using hard coded issuer key/DID and VC definition)
        val issueResult = issuerRepo.openId4VcJwtIssue(issueRequest)
        val credentialOffer = issueResult.getOrThrow()

        // import credential offer into wallet as pending
        val credentialId = walletRepo.useOfferRequest(
            OfferRequest(
                did = did, walletId = walletId,
                credentialOffer = credentialOffer, acceptPending = pending
            )
        ).getOrElse {
            fail(it.message)
        }.firstOrNull()?.credentialId ?: fail("No credential found")
        return Pair(walletId, credentialId)
    }

    private suspend fun getDefaultDid(walletId: String):String {
         return walletRepo.getDids(walletId).getOrThrow().
            firstOrNull { it.default }?.did
             ?: fail("No default DID found")
    }

    private suspend fun login():String {
        walletRepo.login(loginRequest).onFailure {
            fail(it.message)
        }
        val (_, wallets) = walletRepo.getWallets().getOrElse {
            fail(it.message)
        }
        return wallets.first().id
    }

}