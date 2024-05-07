package ch.healthwallet.tabs.vc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

class VCScreen : Screen {

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {




            Text(text = "Verifiable Credentials")

            Spacer(modifier = Modifier.height(16.dp))

//            FloatingActionButton(
//                onClick = {
//                    navigator.push(QRScannerScreen())
//                },
//            ) {
//                Icon(Icons.Filled.Add, "Scan QR code.")
//            }
            ExtendedFloatingActionButton(
                onClick = { navigator.push(QRScannerScreen()) },
                icon = { Icon(Icons.Filled.Add, "Import VC from QR") },
                text = { Text(text = "Import VC") },
            )

        }
    }

}
