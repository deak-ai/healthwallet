package ch.healthwallet.vc

interface VerificationCallbackHandler {

    suspend fun onStatusCallback(id:String): Result<String>


}