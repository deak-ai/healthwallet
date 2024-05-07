package ch.healthwallet.di

import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.prefs.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun getDatastoreModuleByPlatform() = module {

    single {
        createDataStore(androidContext())
    }

    single {
        AppPrefsRepository(get())
    }

}