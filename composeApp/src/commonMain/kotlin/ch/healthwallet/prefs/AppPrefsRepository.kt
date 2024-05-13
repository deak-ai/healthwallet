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
    private val waltIdEmailKey = stringPreferencesKey("waltid.email")
    private val waltIdPasswordKey = stringPreferencesKey("waltid.password")
    private val waltIdWalletApiKey = stringPreferencesKey("waltid.wallet_api")
    private val waltIdPrefsValidKey = booleanPreferencesKey("waltid.prefs_valid")

    val settings: Flow<AppPrefs> = dataStore.data.map {
        AppPrefs(
            it[waltIdPrefsValidKey] ?: AppPrefs.DEFAULT_APP_PREFS_VALID,
            it[waltIdEmailKey] ?: AppPrefs.DEFAULT_EMAIL,
            it[waltIdPasswordKey] ?: AppPrefs.DEFAULT_PASSWORD,
            it[waltIdWalletApiKey] ?: AppPrefs.DEFAULT_WALTID_WALLET_API
        )
    }

    suspend fun setAppPrefsValid() {
        dataStore.edit { it[waltIdPrefsValidKey] = true }
    }

    suspend fun saveSettings(
        waltIdWalletApi: String,
        waltIdEmail: String,
        waltIdPassword: String
    ) {
        dataStore.edit {
            it[waltIdEmailKey] = waltIdEmail
            it[waltIdPasswordKey] = waltIdPassword
            it[waltIdWalletApiKey] = waltIdWalletApi
        }
    }
}