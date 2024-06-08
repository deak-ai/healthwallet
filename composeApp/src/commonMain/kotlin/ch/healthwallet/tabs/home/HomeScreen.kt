package ch.healthwallet.tabs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.screen.Screen
import ch.healthwallet.comps.VcTile
import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject

class HomeScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val homeScreenModel = koinInject<HomeScreenModel>()
        val vcs by homeScreenModel.vcList.collectAsState(emptyList())

        val snackbarHostState =  remember { SnackbarHostState() }


        val errorMessage by homeScreenModel.errorMessage.collectAsState()

        // Trigger refresh when the composable enters composition
        LaunchedEffect(Unit) {
            homeScreenModel.refresh()
        }

        // Show Snackbar when there's an error message
        LaunchedEffect(errorMessage) {
            errorMessage?.let {msg ->
                snackbarHostState.showSnackbar(
                    message = msg,
                    withDismissAction = true,
                    duration = SnackbarDuration.Long)
                homeScreenModel.clearErrorMessage()
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { homeScreenModel.refresh() },
                    icon = { Icon(Icons.Filled.Refresh, "Load credentials") },
                    text = { Text(text = "Reload") },
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 200.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding()) // Ensure bottom padding
                    ) {
                        items(vcs) { vc ->
                            VcTile(vc)
                        }
                    }

                }
            }
        )
    }

}
