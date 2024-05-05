package ai.deak.shw.repo

import ai.deak.shw.data.LoginRequest
import ai.deak.shw.data.LoginResponse

interface WaltIdWalletRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>

}



