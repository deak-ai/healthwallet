package ch.healthwallet.repo

interface WaltIdWalletRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun createUser(createRequest : CreateUserRequest): Result<Boolean>

    suspend fun getUserId(): Result<String>

    suspend fun logout(): Result<Boolean>

    suspend fun getWallets(): Result<WalletList>

    suspend fun getDids(walletId: String): Result<List<DidDetail>>

    suspend fun queryCredentials(credentialsQuery: CredentialsQuery): Result<List<VerifiedCredential>>

    suspend fun getCredential(credentialRequest: CredentialRequest): Result<VerifiedCredential>

    suspend fun useOfferRequest(offerRequest: OfferRequest): Result<List<VerifiedCredential>>

    suspend fun acceptCredential(credentialRequest: CredentialRequest): Result<Boolean>

    suspend fun rejectCredential(credentialRequest: CredentialRequest, reason: String): Result<Boolean>

}



