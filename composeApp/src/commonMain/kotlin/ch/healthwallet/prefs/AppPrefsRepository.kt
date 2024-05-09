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
    private val emailKey = stringPreferencesKey("email")
    private val passwordKey = stringPreferencesKey("password")
    private val waltidWalletApiKey = stringPreferencesKey("waltid_wallet_api")
    private val initialSetupDoneKey = booleanPreferencesKey("initial_setup_done")

    val settings: Flow<AppPrefs> = dataStore.data.map {
        AppPrefs(
            it[initialSetupDoneKey] ?: AppPrefs.DEFAULT_INITIAL_SETUP_DONE,
            it[emailKey] ?: AppPrefs.DEFAULT_EMAIL,
            it[passwordKey] ?: AppPrefs.DEFAULT_PASSWORD,
            it[waltidWalletApiKey] ?: AppPrefs.DEFAULT_WALTID_WALLET_API
        )
    }

    suspend fun initialSetupDone() {
        dataStore.edit { it[initialSetupDoneKey] = true }
    }

    suspend fun saveSettings(
        waltIdWalletApi: String,
        email: String,
        password: String
    ) {
        dataStore.edit {
            it[emailKey] = email
            it[passwordKey] = password
            it[waltidWalletApiKey] = waltIdWalletApi
        }
    }
}