package com.example.fay

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fay.appointments.ui.AppointmentsScreen
import com.example.fay.auth.ui.login.LoginScreen
import com.example.fay.chat.ui.ChatScreen
import com.example.fay.core.navigation.Screen
import com.example.fay.journal.ui.JournalScreen
import com.example.fay.profile.ui.ProfileScreen

@Composable
fun FayNavHost(
    authenticated: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (authenticated) Screen.Chat.route else Screen.Login.route,
        modifier = modifier
    ) {
        // Auth flow (placeholder for now)
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Appointments.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // TODO: Replace with AppointmentsNavGraph
        composable(Screen.Appointments.route) {
            AppointmentsScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Journal.route) {
            JournalScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}