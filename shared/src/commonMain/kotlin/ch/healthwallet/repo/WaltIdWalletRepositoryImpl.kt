package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.StateFlow

class WaltIdWalletRepositoryImpl(
    private val httpClient: HttpClient,
    private val prefsFlow : StateFlow<WaltIdPrefs>
): WaltIdWalletRepository {

    override suspend fun login(): Result<LoginResponse> {
        return try {
            val appPrefs = prefsFlow.value
            val baseUrl = appPrefs.waltIdWalletApi
            val response: HttpResponse = httpClient.post(
                "$baseUrl/wallet-api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email = appPrefs.waltIdEmail,
                    password = appPrefs.waltIdPassword))
                expectSuccess = true
            }
            Result.success(response.body<LoginResponse>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun createUser(): Result<Boolean> {
        return try {
            val appPrefs = prefsFlow.value
            val baseUrl = appPrefs.waltIdWalletApi
            httpClient.post("$baseUrl/wallet-api/auth/create") {
                contentType(ContentType.Application.Json)
                // using email also as username
                setBody(CreateUserRequest(appPrefs.waltIdEmail, appPrefs.waltIdEmail,appPrefs.waltIdPassword))
                expectSuccess = true
            }
            Result.success(true)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getUserId(): Result<String> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/wallet-api/auth/user-info") {
                expectSuccess = true
            }
            Result.success(response.body<String>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }



    override suspend fun logout(): Result<Boolean> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            httpClient.post("$baseUrl/wallet-api/auth/logout") {
                expectSuccess = true
            }
            Result.success(true)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getWallets(): Result<WalletList> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/wallet-api/wallet/accounts/wallets") {
                expectSuccess = true
            }
            Result.success(response.body<WalletList>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getDids(walletId: String): Result<List<DidDetail>> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/wallet-api/wallet/${walletId}/dids") {
                expectSuccess = true
            }
            Result.success(response.body<List<DidDetail>>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun queryCredentials(credentialsQuery: CredentialsQuery):
            Result<List<VerifiedCredential>> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/wallet-api/wallet/${credentialsQuery.walletId}/credentials") {
                expectSuccess = true
                parameter("showDeleted", credentialsQuery.showDeleted)
                parameter("showPending", credentialsQuery.showPending)
                parameter("sortBy", credentialsQuery.sortBy)
                parameter("descending", credentialsQuery.sortDescending)
            }
            Result.success(response.body<List<VerifiedCredential>>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getCredential(credentialRequest: CredentialRequest):
            Result<VerifiedCredential> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/wallet-api/wallet/${credentialRequest.walletId}" +
                        "/credentials/${credentialRequest.credentialId}") {
                expectSuccess = true
            }
            Result.success(response.body<VerifiedCredential>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun useOfferRequest(offerRequest: OfferRequest): Result<List<VerifiedCredential>> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.post(
                "$baseUrl/wallet-api/wallet/${offerRequest.walletId}" +
                        "/exchange/useOfferRequest?did=${offerRequest.did}&" +
                        "requireUserInput=${offerRequest.acceptPending}") {
                    contentType(ContentType.Text.Plain)
                    setBody(offerRequest.credentialOffer)
                    expectSuccess = true
                }
            Result.success(response.body<List<VerifiedCredential>>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun acceptCredential(credentialRequest: CredentialRequest): Result<Boolean> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            httpClient.post("$baseUrl/wallet-api/wallet/${credentialRequest.walletId}" +
                    "/credentials/${credentialRequest.credentialId}/accept") {
                expectSuccess = true
            }
            Result.success(true)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun rejectCredential(credentialRequest: CredentialRequest,
                                          reason: String): Result<Boolean> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            httpClient.post("$baseUrl/wallet-api/wallet/${credentialRequest.walletId}" +
                    "/credentials/${credentialRequest.credentialId}/reject") {
                contentType(ContentType.Application.Json)
                setBody(RejectNote(reason))
                expectSuccess = true
            }
            Result.success(true)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }


}