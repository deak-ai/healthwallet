package tabs.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

class LegalScreen : Screen {

    @Composable
    override fun Content() {

        val navigator: Navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Legal") },
                    navigationIcon = {
                        Button(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                            "Sed non risus. Suspendisse lectus tortor, dignissim sit amet, " +
                            "adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. " +
                            "Maecenas ligula massa, varius a, semper congue, euismod non, mi. " +
                            "Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, " +
                            "non fermentum diam nisl sit amet erat. Duis semper. Duis arcu massa, " +
                            "scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue. " +
                            "Ut in risus volutpat libero pharetra tempor. Cras vestibulum bibendum " +
                            "augue. Praesent egestas leo in pede. Praesent blandit odio eu enim. " +
                            "Pellentesque sed dui ut augue blandit sodales. Vestibulum ante ipsum " +
                            "primis in faucibus orci luctus et ultrices posuere cubilia Curae; " +
                            "Aliquam nibh. Mauris ac mauris sed pede pellentesque fermentum. " +
                            "Maecenas adipiscing ante non diam sodales hendrerit.",
                )

            }
        }
    }
}
