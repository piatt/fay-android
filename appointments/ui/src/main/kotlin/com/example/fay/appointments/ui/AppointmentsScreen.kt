package com.example.fay.appointments.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fay.appointments.domain.AppointmentState
import com.example.fay.core.ui.components.CenteredCallout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    state: AppointmentsUiState,
    onRefresh: () -> Unit,
    onCreateNewAppointment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.appointments_label),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {
                    OutlinedButton(
                        onClick = onCreateNewAppointment,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_new_appt),
                            contentDescription = stringResource(R.string.new_appointment_description)
                        )
                        Spacer(Modifier.padding(4.dp))
                        Text(
                            text = stringResource(R.string.new_appointment_button_label),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.loading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.errorMessage != null) {
                CenteredCallout(
                    image = R.drawable.ic_calendar,
                    message = state.errorMessage
                )
            } else {
                val pagerState = rememberPagerState(pageCount = { 2 })
                val scope = rememberCoroutineScope()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            text = { Text(stringResource(R.string.upcoming_appointments_label)) }
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            text = { Text(stringResource(R.string.past_appointments_label)) }
                        )
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> AppointmentsList(
                                appointments = state.upcomingAppointments,
                                highlightFirstAppointment = true
                            )
                            1 -> AppointmentsList(
                                appointments = state.pastAppointments,
                                highlightFirstAppointment = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<AppointmentState>,
    highlightFirstAppointment: Boolean,
    modifier: Modifier = Modifier
) {
    if (appointments.isEmpty()) {
        CenteredCallout(
            image = R.drawable.ic_calendar,
            message = stringResource(R.string.coming_soon_label),
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(
                items = appointments,
                key = { _, appointment -> appointment.id }
            ) { index, appointment ->
                AppointmentCard(
                    appointment = appointment,
                    highlightCard = highlightFirstAppointment && index == 0,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentState,
    highlightCard: Boolean,
    modifier: Modifier = Modifier
) {
    if (highlightCard) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp)
        ) {
            AppointmentCardContent(
                appointment = appointment,
                showJoinAppointmentButton = true,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        OutlinedButton(
            onClick = {},
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            AppointmentCardContent(
                appointment = appointment,
                showJoinAppointmentButton = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}

@Composable
private fun AppointmentCardContent(
    appointment: AppointmentState,
    showJoinAppointmentButton: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = appointment.formattedMonth,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = appointment.formattedDay,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.formattedTimeRange,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appointment.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (showJoinAppointmentButton) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = stringResource(R.string.join_appointment_label),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.join_appointment_label))
            }
        }
    }
}