package ai.deak.shw.repo

import ai.deak.shw.data.LoginRequest
import ai.deak.shw.data.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
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
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: ClientRequestException) {
            // Handle 400/401 responses
            Result.failure(RuntimeException(e.response.bodyAsText()))
        } catch (e: Exception) {
            // Handle other exceptions
            Result.failure(e)
        }
    }

}