package com.example.fay.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fay.FayAppState
import com.example.fay.appointments.ui.AppointmentsRoute
import com.example.fay.appointments.ui.AppointmentsScreen
import com.example.fay.auth.ui.LoginRoute
import com.example.fay.auth.ui.LoginScreen
import com.example.fay.auth.ui.LoginViewModel
import com.example.fay.chat.ui.ChatRoute
import com.example.fay.chat.ui.ChatScreen
import com.example.fay.journal.ui.JournalRoute
import com.example.fay.journal.ui.JournalScreen
import com.example.fay.profile.ui.ProfileRoute
import com.example.fay.profile.ui.ProfileScreen

@Composable
fun FayNavHost(
    state: FayAppState,
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (state.authenticated) AppointmentsRoute else LoginRoute,
        modifier = modifier
    ) {
        composable<LoginRoute> {
            val viewModel = hiltViewModel<LoginViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                onCredentialsModified = viewModel::clearError,
                onLoginAttempt = viewModel::login,
                onLoginSuccess = {
                    navController.navigate(AppointmentsRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }
        // TODO: Replace with AppointmentsNavGraph
        composable<AppointmentsRoute> {
            AppointmentsScreen()
        }
        composable<ChatRoute> {
            ChatScreen()
        }
        composable<JournalRoute> {
            JournalScreen()
        }
        composable<ProfileRoute> {
            ProfileScreen(
                onLogout = {
                    onLogout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}