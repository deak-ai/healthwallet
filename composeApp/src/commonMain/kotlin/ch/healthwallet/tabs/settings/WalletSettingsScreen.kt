package ch.healthwallet.tabs.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
class WalletSettingsScreen() : Screen {

    // TODO: extract strings to resources (strings.xml)
    @Composable
    override fun Content() {
        val walletSettingsScreenModel = koinInject<WalletSettingsScreenModel>()
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { walletSettingsScreenModel }
        val snackbarHostState =  remember { SnackbarHostState() }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            //TODO: figure out why a floatingActionButton is needed for the Snackbar to show
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Dummy") },
                    icon = { Icon(Icons.Filled.Info, contentDescription = "") },
                    onClick = {
                        println("Floating button pressed")
                    }
                )
            },
            topBar = {
                TopAppBar(
                    title = { Text("Wallet Settings") },
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
        ) {innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    value = viewModel.waltidWalletApi,
                    onValueChange = { viewModel.updateWaltIdWalletApi(it) },
                    label = { Text("Walt.id Wallet API") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    singleLine = true,
                    isError = viewModel.waltidWalletApiHasErrors,
                    supportingText = {
                        if (viewModel.waltidWalletApiHasErrors) {
                            Text("Incorrect URL format.")
                        }
                    }

                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.waltIdEmail,
                    onValueChange = { viewModel.updateWaltIdEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    singleLine = true,
                    isError = viewModel.waltIdEmailHasErrors,
                    supportingText = {
                        if (viewModel.waltIdEmailHasErrors) {
                            Text("Incorrect email format.")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    // TODO: password should be stored encrypted
                    value = viewModel.waltIdPassword,
                    onValueChange = { viewModel.updateWaltIdPassword(it) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (viewModel.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    trailingIcon = {
                        // TODO: use proper icons (with "eye") for password visibility
                        // TODO: password visibility should be set false after navigating away
                        val image = if (viewModel.passwordVisibility)
                            Icons.Default.Lock
                        else
                            Icons.Default.Info
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(image, contentDescription = "Toggle password visibility")
                        }
                    },
                    singleLine = true,
                    isError = viewModel.waltIdPasswordHasErrors,
                    supportingText = {
                        if (viewModel.waltIdPasswordHasErrors) {
                            Text("No password set.")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveSettings()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.settingsChanged && viewModel.waltIdAppPrefsScreenValid
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.testConnection(snackbarHostState)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true

                ) {
                    Text("Test")
                }
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }

}
