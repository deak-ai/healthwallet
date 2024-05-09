package ch.healthwallet.prefs

data class AppPrefs(
    val initialSetupDone: Boolean = DEFAULT_INITIAL_SETUP_DONE,
    val email: String = DEFAULT_EMAIL,
    val password: String = DEFAULT_PASSWORD,
    val waltidWalletApi: String = DEFAULT_WALTID_WALLET_API,
) {
    companion object {
        const val DEFAULT_INITIAL_SETUP_DONE = false
        const val DEFAULT_EMAIL = ""
        const val DEFAULT_PASSWORD = ""
        const val DEFAULT_WALTID_WALLET_API = "http://localhost:7001"
    }
}