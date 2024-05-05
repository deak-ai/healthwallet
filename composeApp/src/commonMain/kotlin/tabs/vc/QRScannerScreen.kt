package tabs.vc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import qrscanner.QrScanner

@OptIn(ExperimentalMaterial3Api::class)
class QRScannerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Scan QR code") },
                    navigationIcon = {
                        Button(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                )
            }
        ) { innerPadding: PaddingValues ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {

                QrScanner(
                    modifier = Modifier
                        .clipToBounds()
                        .clip(shape = RoundedCornerShape(size = 14.dp)),
                    flashlightOn = false,
                    launchGallery = false,
                    onCompletion = {
                        println("QR Scan Success: $it")
                    },
                    onGalleryCallBackHandler = {
                        println("GalleryCallbankHandler: $it")
                    },
                    onFailure = {
                        println("QR Scan Failure: $it")
                    }
                )
            }
        }
    }
}
