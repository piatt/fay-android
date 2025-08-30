package com.example.fay.appointments.ui

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.appointmentsNavGraph(navController: NavHostController) {
    composable<AppointmentsRoute> {
        val viewModel = hiltViewModel<AppointmentsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        AppointmentsScreen(
            state = state,
            onRefresh = viewModel::refresh,
            onCreateNewAppointment = {
                navController.navigate(NewAppointmentRoute)
            }
        )
    }
    composable<NewAppointmentRoute> {
        NewAppointmentScreen(
            onNavigateBack = navController::navigateUp
        )
    }
}