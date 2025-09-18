package com.darekbx.emailbot.ui.configuration.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.emailbot.R
import com.darekbx.emailbot.model.ConfigurationInfo
import com.darekbx.emailbot.ui.ErrorView
import com.darekbx.emailbot.ui.ProgressView
import com.darekbx.emailbot.ui.theme.EmailBotTheme

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
    onConfigurationSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var configurationInfo by remember { mutableStateOf(ConfigurationInfo.EMPTY) }
    var checkPassed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchConfiguration()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ConfigurationView(
            configurationInfo = configurationInfo,
            checkPassed = checkPassed,
            onCheckConnection = { config -> viewModel.checkConnection(config) },
            onSave = { config -> viewModel.saveConfiguration(config) }
        )
        when (val state = uiState) {
            is ConfigurationUiState.Idle -> { /* NOOP */ }
            is ConfigurationUiState.Loading -> ProgressView()
            is ConfigurationUiState.Error -> ErrorView(state.e) { viewModel.resetState() }
            is ConfigurationUiState.Success -> configurationInfo = state.configurationInfo
            is ConfigurationUiState.CheckResult -> {
                if (state.result) {
                    checkPassed = true
                } else {
                    ShowConfigurationErrorToast()
                }
            }
            is ConfigurationUiState.Saved -> onConfigurationSaved()
        }
    }
}

@Composable
private fun ShowConfigurationErrorToast() {
    val context = LocalContext.current
    Toast.makeText(
        context,
        "Failed to connect using provided configuration",
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
private fun ConfigurationView(
    configurationInfo: ConfigurationInfo,
    checkPassed: Boolean = false,
    onCheckConnection: (ConfigurationInfo) -> Unit = {},
    onSave: (ConfigurationInfo) -> Unit = {},
) {
    var email = remember { mutableStateOf(configurationInfo.email) }
    var password = remember { mutableStateOf(configurationInfo.password) }
    var imapHost = remember { mutableStateOf(configurationInfo.imapHost) }
    var imapPort = remember { mutableIntStateOf(configurationInfo.imapPort) }

    fun buildConfigurationInfo(): ConfigurationInfo {
        return ConfigurationInfo(email.value, password.value, imapHost.value, imapPort.intValue)
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Logo()
            Title()
        }
        InputsCard(email, password, imapHost, imapPort)
        ActionButtons(
            checkPassed = checkPassed,
            onCheckConnection = { onCheckConnection(buildConfigurationInfo()) },
            onSave = { onSave(buildConfigurationInfo()) }
        )
    }
}

@Composable
private fun ActionButtons(
    checkPassed: Boolean,
    onCheckConnection: () -> Unit,
    onSave: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, bottom = 8.dp),
            enabled = !checkPassed,
            onClick = onCheckConnection
        ) {
            Text("Check connection")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            enabled = checkPassed,
            onClick = onSave
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun InputsCard(
    email: MutableState<String>,
    password: MutableState<String>,
    imapHost: MutableState<String>,
    imapPort: MutableState<Int>,
) {
    val isPasswordVisible = remember { mutableStateOf(false) }
    Card(
        Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email account") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (isPasswordVisible.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible.value) R.drawable.ic_visibility_on
                                else R.drawable.ic_visibility_off
                            ),
                            contentDescription =
                                if (isPasswordVisible.value) "Hide password" else "Show password"
                        )
                    }
                }
            )
            OutlinedTextField(
                value = imapHost.value,
                onValueChange = { imapHost.value = it },
                label = { Text("Imap host") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "${imapPort.value}",
                onValueChange = { imapPort.value = it.toInt() },
                label = { Text("Imap port") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Logo() {
    Image(
        modifier = Modifier
            .padding(32.dp)
            .background(MaterialTheme.colorScheme.surfaceDim, CircleShape)
            .size(128.dp),
        painter = painterResource(R.drawable.email_bot_logo),
        contentDescription = "logo"
    )
}

@Composable
private fun Title() {
    Text(
        modifier = Modifier,
        text = "Email Configuration",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Preview
@Composable
private fun ErrorViewPreview() {
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ErrorView(IllegalStateException("Mock error message"))
        }
    }
}

@Preview
@Composable
private fun ConfigurationPreview() {
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ConfigurationView(ConfigurationInfo.EMPTY)
        }
    }
}
