import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import smarthealthwallet.composeapp.generated.resources.Res
import smarthealthwallet.composeapp.generated.resources.compose_multiplatform
import smarthealthwallet.composeapp.generated.resources.shw_logo

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun SampleApp() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.shw_logo),
                        contentDescription = null
                    )
                    //Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")

                    KamelImage(
                        asyncPainterResource("https://sebi.io/demo-image-api/eagle/alfred-kenneally-69JBZWDa1r8-unsplash.jpg"),
                        "An Eagle",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)
                    )

                }
            }
        }
    }
}