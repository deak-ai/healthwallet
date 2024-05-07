package ch.healthwallet.repo

interface WaltIdWalletRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun createUser(createRequest : CreateUserRequest): Result<Boolean>

    suspend fun getUserId(): Result<String>

    suspend fun logout(): Result<Boolean>

    suspend fun getWallets(): Result<WalletList>

    suspend fun queryCredentials(credentialsQuery: CredentialsQuery): Result<List<VerifiedCredential>>

    suspend fun getCredential(credentialRequest: CredentialRequest): Result<VerifiedCredential>

}



