package com.example.fay.components

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.fay.FayAppState
import com.example.fay.core.ui.theme.FayTheme

@Composable
fun FayApp(
    state: FayAppState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.loading) {
        FayTheme {
            val navController = rememberNavController()
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
                    onLogout = onLogout,
                    modifier = Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                )
            }
        }
    }
}

@Preview
@Composable
private fun FayAppPreview() {
    FayApp(
        state = FayAppState(),
        onLogout = {}
    )
}