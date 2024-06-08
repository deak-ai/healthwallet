package ch.healthwallet.repo

import io.ktor.http.Url
import kotlin.Result

interface WaltIdVerifierRepository {
    suspend fun verify(verifyRequest: VerifyRequest): Result<Url>

}



