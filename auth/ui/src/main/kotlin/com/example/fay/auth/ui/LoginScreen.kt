package com.example.fay.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: LoginUiState,
    onCredentialsModified: () -> Unit,
    onLoginAttempt: (String, String) -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.authenticated) {
        onLoginSuccess()
    } else {
        var username by rememberSaveable { mutableStateOf("")}
        var password by rememberSaveable { mutableStateOf("")}
        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.fay_logo),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    onCredentialsModified()
                },
                label = { Text(stringResource(R.string.username_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    onCredentialsModified()
                },
                label = { Text(stringResource(R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading
            )
            Button(
                onClick = {
                    onLoginAttempt(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(56.dp),
                enabled = !state.loading
            ) {
                if (state.loading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.login_label))
                }
            }
            Text(
                text = state.errorMessage ?: "",
                color = colorResource(com.example.fay.core.ui.R.color.fay_primary)
            )
        }
    }
}

