package com.example.fay.appointments.ui

import com.example.fay.core.navigation.BottomNavItem
import com.example.fay.core.navigation.Screen

class AppointmentsNavItem : BottomNavItem(
    screen = Screen.Appointments,
    label = R.string.appointments_label,
    icon = R.drawable.ic_calendar,
    selectedIcon = R.drawable.ic_calendar_filled
)