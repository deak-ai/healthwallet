package ch.healthwallet.tabs.vc

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ch.healthwallet.composables.SelectableVcTile
import ch.healthwallet.tabs.home.HomeScreenModel
import ch.healthwallet.tabs.home.HomeTab
import org.koin.compose.koinInject

class VCScreen : Screen {

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val tabNavigator: TabNavigator = LocalTabNavigator.current
        val vcScreenModel = koinInject<VCScreenModel>()
        val homeScreenModel = koinInject<HomeScreenModel>()
        val state by vcScreenModel.state.collectAsState()
        when (state) {
            is VCScanState.Initial -> InitialScreen(navigator, vcScreenModel)
            is VCScanState.Processing -> ProcessingScreen()

            // credential offer states
             is VCScanState.AcceptPrescription -> ConfirmationDialog(
                title = "Import Prescription?",
                message = (state as VCScanState.AcceptPrescription).medRefData.nameDe,
                onAccept = { vcScreenModel.handleEvent(VCEvent.AcceptCredential((state as VCScanState.AcceptPrescription).credentialRequest)) },
                onReject = { vcScreenModel.handleEvent(VCEvent.RejectCredential((state as VCScanState.AcceptPrescription).credentialRequest)) }
            )
            is VCScanState.Error -> ErrorScreen((state as VCScanState.Error).message) { vcScreenModel.handleEvent(VCEvent.Reset) }

            is VCScanState.CredentialImported -> {
                // Switch to Home tab
                LaunchedEffect(Unit) {
                    tabNavigator.current = HomeTab
                    vcScreenModel.handleEvent(VCEvent.Reset)
                }
            }

            // presentation states
            is VCScanState.SelectCredential -> SelectCredentialScreen(
                (state as VCScanState.SelectCredential).prescriptions,
                null
            ) { ps -> vcScreenModel.onPrescriptionSelected(ps) }

            is VCScanState.PresentationCompleted -> {
                // Switch to Home tab
                LaunchedEffect(Unit) {
                    tabNavigator.current = HomeTab
                    homeScreenModel.setSnackbarMessage("Prescription presented successfully.")
                    vcScreenModel.handleEvent(VCEvent.Reset)
                }
            }

        }
    }


    @Composable
    fun SelectCredentialScreen(
        prescriptions: List<VCScreenModel.PrescriptionSelection>,
        selectedPrescription: VCScreenModel.PrescriptionSelection?,
        onSubmit: (VCScreenModel.PrescriptionSelection) -> Unit
    ) {
        var selected by remember { mutableStateOf(selectedPrescription) }

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Please select Prescription",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(prescriptions) { prescription ->
                    SelectableVcTile(
                        selection = prescription,
                        isSelected = prescription == selected,
                        onSelected = {
                            selected = prescription
                        }
                    )
                }
            }
            Button(
                onClick = {
                    selected?.let {
                        onSubmit(it)
                    }
                },
                enabled = selected != null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Submit")
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
            Text(text = "Manage Prescriptions")
            Spacer(modifier = Modifier.height(16.dp))
            ExtendedFloatingActionButton(
                onClick = { navigator.push(QRScannerScreen(vcScreenModel)) },
                icon = { Icon(Icons.Filled.Add, "Scan a QR Code") },
                text = { Text(text = "Scan a QR Code") },
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            //CircularProgressIndicator()
            AnimatedInfiniteProgressIndicator()
        }
    }

    @Composable
    fun AnimatedInfiniteProgressIndicator(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.primary,
        strokeWidth: Float = 4f
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        Canvas(modifier = modifier.size(48.dp)) {
            drawArc(
                color = color,
                startAngle = angle,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}
