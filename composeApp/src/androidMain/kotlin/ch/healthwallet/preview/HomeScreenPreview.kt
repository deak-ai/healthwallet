package ch.healthwallet.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.healthwallet.tabs.home.HomeScreen
import ch.healthwallet.tabs.settings.DisclaimerScreen
import ch.healthwallet.tabs.settings.WalletSettingsScreen




@Preview
@Composable
fun HomeScreenTest() {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /*TODO*/ },)
            {

            }
        },

    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        )
        {
            items(5) { index ->
                Text(text = "Item: $index")
            }
        }
    }
}
