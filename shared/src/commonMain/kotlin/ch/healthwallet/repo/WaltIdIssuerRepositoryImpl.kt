package ch.healthwallet.repo

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.Result

class WaltIdIssuerRepositoryImpl(
    private val httpClient: HttpClient,
    private val prefsFlow : StateFlow<WaltIdPrefs>): WaltIdIssuerRepository {

    override suspend fun openId4VcJwtIssue(issueRequest: OpenId4VcJwtIssueRequest): Result<String> {
        return try {
            val baseUrl = prefsFlow.value.waltIdWalletApi
            val response: HttpResponse = httpClient.post("$baseUrl/openid4vc/jwt/issue") {
                contentType(ContentType.Application.Json)
                setBody(issueRequest)
                expectSuccess = true
            }
            Result.success(response.body<String>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}