package ch.healthwallet.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import ch.healthwallet.repo.AppPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class AppPrefsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val waltIdEmailKey = stringPreferencesKey("waltid.email")
    private val waltIdPasswordKey = stringPreferencesKey("waltid.password")
    private val waltIdWalletApiKey = stringPreferencesKey("waltid.wallet_api")
    private val waltIdPrefsValidKey = booleanPreferencesKey("waltid.prefs_valid")

    private val _appPrefs = MutableStateFlow(AppPrefs())
    val appPrefs: StateFlow<AppPrefs> = _appPrefs.asStateFlow()

    init {
        println("Initializing AppPrefsRepository")
        runBlocking {
            dataStore.data
                .map {
                    AppPrefs(
                        it[waltIdPrefsValidKey] ?: AppPrefs.DEFAULT_APP_PREFS_VALID,
                        it[waltIdEmailKey] ?: AppPrefs.DEFAULT_EMAIL,
                        it[waltIdPasswordKey] ?: AppPrefs.DEFAULT_PASSWORD,
                        it[waltIdWalletApiKey] ?: AppPrefs.DEFAULT_WALTID_WALLET_API
                    )
                }
                .firstOrNull()?.let {
                    println("SETTING INITIAL PREFERENCES to $it")
                    _appPrefs.value = it
                }
        }
    }


    suspend fun setAppPrefsValid() {
        dataStore.edit { it[waltIdPrefsValidKey] = true }
        _appPrefs.value = _appPrefs.value.copy(waltIdPrefsValid = true)
    }

    suspend fun saveSettings(
        waltIdWalletApi: String,
        waltIdEmail: String,
        waltIdPassword: String
    ) {
        println("SAVING SETTINGS: $waltIdWalletApi, $waltIdEmail, $waltIdPassword")
        dataStore.edit {
            it[waltIdEmailKey] = waltIdEmail
            it[waltIdPasswordKey] = waltIdPassword
            it[waltIdWalletApiKey] = waltIdWalletApi
        }
        _appPrefs.value = _appPrefs.value.copy(
            waltIdWalletApi = waltIdWalletApi,
            waltIdEmail = waltIdEmail,
            waltIdPassword = waltIdPassword
        )
    }


}