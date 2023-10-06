package com.darekbx.infopigula.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.InformationDialog
import com.darekbx.infopigula.ui.ProgressIndicator
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun LoginScreen(
    loginViewModel: DefaultLoginViewModel = hiltViewModel(),
    loginSuccessfull: () -> Unit
) {
    LoginScreenWrapper(loginViewModel, loginSuccessfull)
}

@Composable
fun LoginScreenWrapper(
    loginViewModel: LoginViewModel = hiltViewModel(),
    loginSuccessfull: () -> Unit
) {
    val state by loginViewModel.uiState.collectAsState(initial = LoginUiState.Idle)

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state == LoginUiState.Done) {
            loginSuccessfull()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val modifier = if (state == LoginUiState.InProgress) Modifier.blur(5.dp) else Modifier
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Sign in", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = loginViewModel.email,
                    isError = emailError,
                    label = { Text(text = "E-mail") },
                    onValueChange = {
                        if (emailError) {
                            emailError = false
                        }
                        loginViewModel.updateEmail(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = loginViewModel.password,
                    label = { Text(text = "Password") },
                    isError = passwordError,
                    onValueChange = {
                        if (passwordError) {
                            passwordError = false
                        }
                        loginViewModel.updatePassword(it)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!loginViewModel.isEmailValid()) {
                            emailError = true
                        } else if (!loginViewModel.isPasswordValid()) {
                            passwordError = true
                        } else {
                            loginViewModel.login()
                        }
                    }
                ) {
                    Text(text = "Login", color = MaterialTheme.colorScheme.background)
                }
            }
        }

        if (state == LoginUiState.InProgress) {
            ProgressIndicator()
        }

        if (state is LoginUiState.Failed) {
            val failedState = state as LoginUiState.Failed
            InformationDialog(message = "Failed to login! (${failedState.message})") {
                loginViewModel.reset()
            }
        }
    }
}


@Preview
@Composable
fun LoginPreviewLight() {
    InfoPigulaTheme(isDarkTheme = false) {
        Surface {
            LoginScreenWrapper(object : LoginViewModel {
                override val uiState = emptyFlow<LoginUiState>()
                override var email: String = ""
                override var password: String = ""
                override fun updateEmail(input: String) {}
                override fun updatePassword(input: String) {}
                override fun login() {}
                override fun reset() {}
                override fun isEmailValid() = true
                override fun isPasswordValid() = false
            }) { }
        }
    }
}

@Preview
@Composable
fun LoginPreviewDark() {
    InfoPigulaTheme(isDarkTheme = true) {
        Surface {
            LoginScreenWrapper(object : LoginViewModel {
                override val uiState = emptyFlow<LoginUiState>()
                override var email: String = ""
                override var password: String = ""
                override fun updateEmail(input: String) {}
                override fun updatePassword(input: String) {}
                override fun login() {}
                override fun reset() {}
                override fun isEmailValid() = true
                override fun isPasswordValid() = false
            }) { }
        }
    }
}