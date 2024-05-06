package ai.deak.shw.repo

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

class WaltIdWalletRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String): WaltIdWalletRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response: HttpResponse = httpClient.post("$baseUrl/wallet-api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
                expectSuccess = true
            }
            Result.success(response.body<LoginResponse>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun createUser(createRequest: CreateUserRequest): Result<Boolean> {
        return try {
            httpClient.post("$baseUrl/wallet-api/auth/create") {
                contentType(ContentType.Application.Json)
                setBody(createRequest)
                expectSuccess = true
            }
            Result.success(true)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getUserId(): Result<String> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/wallet-api/auth/user-info") {
                expectSuccess = true
            }
            Result.success(response.body<String>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }



    override suspend fun logout(): Result<Boolean> {
        return try {
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
            val response: HttpResponse = httpClient.get("$baseUrl/wallet-api/wallet/accounts/wallets") {
                expectSuccess = true
            }
            Result.success(response.body<WalletList>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun queryCredentials(credentialsQuery: CredentialsQuery): Result<List<VerifiedCredential>> {
        return try {
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

    override suspend fun getCredential(credentialRequest: CredentialRequest): Result<VerifiedCredential> {
        return try {
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


}