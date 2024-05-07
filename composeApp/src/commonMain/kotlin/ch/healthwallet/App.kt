package ch.healthwallet

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ch.healthwallet.di.datastoreModule
import ch.healthwallet.di.koinModule
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import ch.healthwallet.tabs.home.HomeTab
import ch.healthwallet.tabs.settings.SettingsTab
import ch.healthwallet.tabs.vc.VCTab

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {

    println("App starting")
    KoinApplication(application = {
        modules(koinModule, datastoreModule)
    }) {
        println("App starting...")
        AppComposition()
    }
}

@Composable
fun AppComposition() {
    MaterialTheme {
        TabNavigator(
            tab = HomeTab
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(VCTab)
                        TabNavigationItem(SettingsTab)
                    }
                },
                content = { CurrentTab() },
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription =
                    tab.options.title
                )
            }
        },
        label = {
            Text(
                text = tab.options.title
            )
        }
    )
}