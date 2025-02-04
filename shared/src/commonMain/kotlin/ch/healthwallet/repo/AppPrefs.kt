package ch.healthwallet.repo

data class AppPrefs(
    val waltIdPrefsValid: Boolean = DEFAULT_APP_PREFS_VALID,
    val waltIdEmail: String = DEFAULT_EMAIL,
    val waltIdPassword: String = DEFAULT_PASSWORD,
    val waltIdWalletApi: String = DEFAULT_WALTID_WALLET_API,
    val waltIdIssuerApi: String = DEFAULT_WALTID_ISSUER_API,
    val waltIdVerifierApi: String = DEFAULT_WALTID_VERIFIER_API,
    val pisServerApi: String = DEFAULT_PIS_SERVER_API,
) {
    companion object {
        const val DEFAULT_APP_PREFS_VALID = false
        const val DEFAULT_EMAIL = ""
        const val DEFAULT_PASSWORD = ""
        const val DEFAULT_WALTID_WALLET_API = "https://wallet.healthwallet.li"
        const val DEFAULT_WALTID_ISSUER_API = "https://issuer.healthwallet.li"
        const val DEFAULT_WALTID_VERIFIER_API = "https://verifier.healthwallet.li"
        const val DEFAULT_PIS_SERVER_API = "https://pis.healthwallet.li"
        const val WALLET_SETTINGS_NOT_SET = "Wallet settings not set"
        const val DEFAULT_VC_NAME = "SwissMedicalPrescription"
        const val DEFAULT_VC_FORMAT = "jwt_vc_json"
    }

    fun isDefault(): Boolean = this == AppPrefs()

}