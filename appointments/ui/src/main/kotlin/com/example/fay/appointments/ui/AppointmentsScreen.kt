package com.example.fay.appointments.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.fay.core.ui.components.CenteredCallout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.appointments_label))
                },
                actions = {
                    IconButton(
                        enabled = true,
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_new_appt),
                            contentDescription = stringResource(R.string.new_appointment_label)
                        )
                    }
                    IconButton(
                        enabled = true,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh_appointments_label)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        CenteredCallout(
            image = R.drawable.ic_calendar,
            message = R.string.appointments_label,
            modifier = Modifier.padding(paddingValues)
        )
    }
}