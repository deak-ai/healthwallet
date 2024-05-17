package ch.healthwallet.prefs

data class AppPrefs(
    val waltIdPrefsValid: Boolean = DEFAULT_APP_PREFS_VALID,
    val waltIdEmail: String = DEFAULT_EMAIL,
    val waltIdPassword: String = DEFAULT_PASSWORD,
    val waltIdWalletApi: String = DEFAULT_WALTID_WALLET_API,
) {
    companion object {
        const val DEFAULT_APP_PREFS_VALID = false
        const val DEFAULT_EMAIL = ""
        const val DEFAULT_PASSWORD = ""
        const val DEFAULT_WALTID_WALLET_API = "https://wallet.walt.id"
        const val WALLET_SETTINGS_NOT_SET = "Wallet settings not set"
    }

    fun isDefault(): Boolean = this == AppPrefs()

}