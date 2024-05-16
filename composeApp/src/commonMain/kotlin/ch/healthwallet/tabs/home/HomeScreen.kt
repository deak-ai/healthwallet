package ch.healthwallet.tabs.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject

class HomeScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val homeScreenModel = koinInject<HomeScreenModel>()
        //val vcs = remember { mutableStateOf(homeScreenModel.vcList.value) }
        val vcs by homeScreenModel.vcList.collectAsState(emptyList())
        Scaffold(
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding()) // Ensure bottom padding
                    ) {
                        items(items = vcs) { vc ->
                            Text(text = vc.credentialId)
                        }
                    }
                    ExtendedFloatingActionButton(
                        onClick = { homeScreenModel.refresh() },
                        icon = { Icon(Icons.Filled.Refresh, "Load credentials") },
                        text = { Text(text = "Reload") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .padding(bottom = innerPadding.calculateBottomPadding()) // Ensure FAB is above navigation bar
                    )
                }
            }
        )
    }

}
