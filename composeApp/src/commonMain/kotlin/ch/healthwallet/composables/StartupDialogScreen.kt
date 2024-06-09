package ch.healthwallet.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ch.healthwallet.tabs.settings.SettingsTab
import ch.healthwallet.tabs.settings.WalletSettingsScreenModel
import org.koin.compose.koinInject


@Composable
fun StartupDialogScreen() {

    val settingsModel = koinInject<WalletSettingsScreenModel>()
    val showStartupDialog by settingsModel.showStartupDialog.collectAsState()

    // keeping the state here to ensure that the dialog is only shown once
    val openAlertDialog = remember(showStartupDialog) { mutableStateOf(showStartupDialog) }
    val navigator = LocalNavigator.currentOrThrow

    if (openAlertDialog.value) {
        StartupDialog(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            dialogTitle = "HealthWallet Setup",
            dialogText = "Please connect your walt.id wallet.",
            onConfirmation = {
                navigator.push(SettingsTab)
                openAlertDialog.value = false
            },
            icon = Icons.Filled.Settings,
            confirmText = "Ok",
        )
    }
}
