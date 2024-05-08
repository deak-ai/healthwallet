package ch.healthwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ch.healthwallet.di.appModule
import ch.healthwallet.di.dataStoreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin() {
            //androidLogger()
            androidContext(this@MainActivity)
            modules(appModule, dataStoreModule)
            setContent {
                App()
            }
        }
    }
}


// just for preview
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}