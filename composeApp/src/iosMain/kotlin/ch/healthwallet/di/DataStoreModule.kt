package ch.healthwallet.di

import ch.healthwallet.prefs.AppPrefsRepository
import ch.healthwallet.prefs.createDataStore
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun getDatastoreModuleByPlatform() = module {

    single {
        createDataStore()
    }

    single {
        AppPrefsRepository(get())
    }

}