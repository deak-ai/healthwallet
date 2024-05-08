package ch.healthwallet.tabs.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject

object SettingsTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        println("Staring setting tab...")
        Navigator(screen = koinInject<SettingsTabScreen>())
        { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}
