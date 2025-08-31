package com.example.fay.appointments.ui

import com.example.fay.core.navigation.BottomNavItem

class AppointmentsNavItem : BottomNavItem(
    appRoute = AppointmentsRoute,
    labelResId = R.string.appointments_label,
    iconResId = R.drawable.ic_calendar,
    selectedIconResId = R.drawable.ic_calendar_filled
)