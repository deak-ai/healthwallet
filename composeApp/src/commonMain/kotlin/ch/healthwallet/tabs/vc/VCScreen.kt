package ch.healthwallet.tabs.vc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import ch.healthwallet.tabs.home.HomeScreen
import ch.healthwallet.tabs.home.HomeTab
import org.koin.compose.koinInject

class VCScreen : Screen {

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val tabNavigator: TabNavigator = LocalTabNavigator.current
        val vcScreenModel = koinInject<VCScreenModel>()
        val state by vcScreenModel.state.collectAsState()
        when (state) {
            is VCScanState.Initial -> InitialScreen(navigator, vcScreenModel)
            is VCScanState.Processing -> ProcessingScreen()
            is VCScanState.ConfirmOffer -> ConfirmationDialog(
                title = "Check Prescription Offer?",
                message = "Details: " + (state as VCScanState.ConfirmOffer).offerInfo,
                onAccept = { vcScreenModel.handleEvent(VCEvent.UseCredentialOffer((state as VCScanState.ConfirmOffer).offerInfo)) },
                onReject = { vcScreenModel.handleEvent(VCEvent.RejectCredentialOffer) }
            )
            is VCScanState.ConfirmCredential -> ConfirmationDialog(
                title = "Import Prescription?",
                message = "Details: "+ (state as VCScanState.ConfirmCredential).credentialRequest,
                onAccept = { vcScreenModel.handleEvent(VCEvent.AcceptCredential((state as VCScanState.ConfirmCredential).credentialRequest)) },
                onReject = { vcScreenModel.handleEvent(VCEvent.RejectCredential((state as VCScanState.ConfirmCredential).credentialRequest)) }
            )
            is VCScanState.Error -> ErrorScreen((state as VCScanState.Error).message) { vcScreenModel.handleEvent(VCEvent.RejectCredentialOffer) }
            is VCScanState.CredentialImported -> {
                // Switch to Home tab
                LaunchedEffect(Unit) {
                    tabNavigator.current = HomeTab
                    vcScreenModel.handleEvent(VCEvent.BackHome)
                }
            }
        }
    }


    @Composable
    fun InitialScreen(navigator: Navigator, vcScreenModel: VCScreenModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Verifiable Credentials")
            Spacer(modifier = Modifier.height(16.dp))
            ExtendedFloatingActionButton(
                onClick = { navigator.push(QRScannerScreen(vcScreenModel)) },
                icon = { Icon(Icons.Filled.Add, "Import VC from QR") },
                text = { Text(text = "Import VC") },
            )
        }
    }

    @Composable
    fun ErrorScreen(message: String, onClick: () -> Unit) {
        AlertDialog(
            onDismissRequest = onClick,
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onClick) {
                    Text("OK")
                }
            },
        )
    }


    @Composable
    fun ConfirmationDialog(title: String, message:  String, onAccept: () -> Unit, onReject: () -> Unit) {
        // Your confirmation dialog UI
        AlertDialog(
            onDismissRequest = onReject,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onAccept) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(onClick = onReject) {
                    Text("Reject")
                }
            }
        )
    }

    @Composable
    fun ProcessingScreen() {
        // Your processing screen UI
        CircularProgressIndicator()
    }

}
