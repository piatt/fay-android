package com.example.fay.components

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.fay.FayAppViewModel
import com.example.fay.core.ui.theme.FayTheme

@Composable
fun FayApp() {
    FayTheme {
        val navController = rememberNavController()
        val viewModel = hiltViewModel<FayAppViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Scaffold(
            bottomBar = {
                if (state.authenticated) {
                    FayBottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            FayNavHost(
                state = state,
                navController = navController,
                onLogout = viewModel::logout,
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            )
        }
    }
}

@Preview
@Composable
private fun FayAppPreview() {
    FayApp()
}