package ch.healthwallet.repo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.coroutines.flow.StateFlow
import kotlin.Result

class WaltIdVerifierRepositoryImpl(
    private val httpClient: HttpClient,
    private val prefsFlow : StateFlow<WaltIdPrefs>
):WaltIdVerifierRepository {
    override suspend fun verify(verifyRequest: VerifyRequest): Result<Url> {
        return try {
            val appPrefs = prefsFlow.value
            val baseUrl = appPrefs.waltIdWalletApi
            val response: HttpResponse = httpClient.post(
                "$baseUrl/openid4vc/verify") {
                contentType(ContentType.Application.Json)
                headers {
                    verifyRequest.apply {
                        append("authorizeBaseUrl", authorizeBaseUrl)
                        append("responseMode", responseMode)
                        successRedirectUri?.let { append("successRedirectUri", it) }
                        errorRedirectUri?.let {append("errorRedirectUri", it) }
                        statusCallbackUri?.let {append("statusCallbackUri", it) }
                        statusCallbackApiKey?.let {append("statusCallbackApiKey",it) }
                        stateId?.let {append("stateId", it)}
                    }
                }
                setBody(verifyRequest.presentationDefinition)
                expectSuccess = true
            }
            Result.success(Url(response.body<String>()))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}