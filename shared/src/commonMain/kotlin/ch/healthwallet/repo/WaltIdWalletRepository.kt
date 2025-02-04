package ch.healthwallet.repo

import io.ktor.http.Url
import kotlin.Result

interface WaltIdWalletRepository {
    suspend fun login(): kotlin.Result<LoginResponse>

    suspend fun createUser(): Result<Boolean>

    suspend fun getUserId(): Result<String>

    suspend fun logout(): Result<Boolean>

    suspend fun getWallets(): Result<WalletList>

    suspend fun getDids(walletId: String): Result<List<DidDetail>>

    suspend fun queryCredentials(credentialsQuery: CredentialsQuery): Result<List<VerifiableCredential>>

    suspend fun getCredential(credentialRequest: CredentialRequest): Result<VerifiableCredential>

    suspend fun useOfferRequest(offerRequest: OfferRequest): Result<List<VerifiableCredential>>

    suspend fun acceptCredential(credentialRequest: CredentialRequest): Result<Boolean>

    suspend fun rejectCredential(credentialRequest: CredentialRequest, reason: String): Result<Boolean>

    suspend fun resolvePresentationRequest(walletId: String, presentationRequest:Url): Result<Url>

    suspend fun matchCredentials(
        walletId: String, presentationFilter: PresentationFilter): Result<List<VerifiableCredential>>

    suspend fun matchCredentials(
        walletId: String, presentationFilter: String): Result<List<VerifiableCredential>>

    suspend fun usePresentationRequest(
        walletId: String, usePresentationRequest: UsePresentationRequest ) : Result<String>

    suspend fun deleteCredential(walletId: String, credentialId: String): Result<Boolean>

}



