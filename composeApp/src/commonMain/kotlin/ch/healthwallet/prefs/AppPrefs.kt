package ch.healthwallet.prefs

data class AppPrefs(
    val darkTheme: Boolean,
    val email: String,
    val password: String,
    val waltidWalletApi: String
)