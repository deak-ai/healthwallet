package ch.healthwallet.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun StartupDialogScreen() {

    val openAlertDialog = remember { mutableStateOf(false) }

    when {
        openAlertDialog.value -> {
            StartupDialog(
                onDismissRequest = {
                    openAlertDialog.value = false
                },
                dialogTitle = "HealthWallet Setup",
                dialogText = "Please connect your walt.id wallet.",
                onConfirmation = {
                    println("Confirmed.")
                },
                icon = Icons.Filled.Settings
            )
        }
    }
}
