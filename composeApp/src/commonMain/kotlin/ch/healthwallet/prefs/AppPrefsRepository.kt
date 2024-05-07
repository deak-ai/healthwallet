package ch.healthwallet.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPrefsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val emailKey = stringPreferencesKey("email")
    private val passwordKey = stringPreferencesKey("password")
    private val waltidWalletApiKey = stringPreferencesKey("waltid_wallet_api")

    companion object {
        const val DEFAULT_DARK_THEME = false
        const val DEFAULT_EMAIL = ""
        const val DEFAULT_PASSWORD = ""
        const val DEFAULT_WALTID_WALLET_API = "http://localhost:7001"
    }


    val settings: Flow<AppPrefs> = dataStore.data.map {
        AppPrefs(
            it[darkThemeKey] ?: DEFAULT_DARK_THEME,
            it[emailKey] ?: DEFAULT_EMAIL,
            it[passwordKey] ?: DEFAULT_PASSWORD,
            it[waltidWalletApiKey] ?: DEFAULT_WALTID_WALLET_API
        )
    }

    suspend fun saveSettings(
        waltIdWalletApi: String,
        email: String,
        password: String,
        darkTheme: Boolean
    ) {
        dataStore.edit {
            it[darkThemeKey] = darkTheme
            it[emailKey] = email
            it[passwordKey] = password
            it[waltidWalletApiKey] = waltIdWalletApi
        }
    }
}