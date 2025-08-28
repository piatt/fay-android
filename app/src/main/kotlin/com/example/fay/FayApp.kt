package com.example.fay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.fay.ui.theme.FayTheme

@Composable
fun FayApp() {
    FayTheme {
        // TODO: Implement real auth check
        val authenticated = true
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (authenticated) {
                    FayBottomNavBar(navController)
                }
            }
        ) { paddingValues ->
            FayNavHost(
                authenticated = authenticated,
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Preview
@Composable
private fun FayAppPreview() {
    FayApp()
}