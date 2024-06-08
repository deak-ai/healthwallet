package ch.healthwallet.repo

import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.StateFlow
import kotlin.Result

class PisServerRepositoryImpl(
    private val httpClient: HttpClient,
    private val prefsFlow : StateFlow<AppPrefs>
):PisServerRepository {
    override suspend fun findMedicamentRefDataBySubstring(substring: String): Result<List<MedicamentRefDataDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun findMedicamentRefDataByGTIN(gtin: String): Result<MedicamentRefDataDTO> {
        return try {
            val baseUrl = prefsFlow.value.pisServerApi
            val response: HttpResponse = httpClient.get(
                "$baseUrl/medications/refdata/$gtin") {
                expectSuccess = true
            }
            Result.success(response.body<MedicamentRefDataDTO>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}