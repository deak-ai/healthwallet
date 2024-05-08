import androidx.compose.ui.window.ComposeUIViewController
import ch.healthwallet.App
import ch.healthwallet.AppComposition
import ch.healthwallet.di.appModule
import ch.healthwallet.di.dataStoreModule
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(appModule, dataStoreModule)
    }) {
        println("App starting...")
        App()
    }
}