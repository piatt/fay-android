package com.example.fay.profile.ui

import com.example.fay.core.navigation.BottomNavItem

class ProfileNavItem : BottomNavItem(
    appRoute = ProfileRoute,
    label = R.string.profile_label,
    icon = R.drawable.ic_user,
    selectedIcon = R.drawable.ic_user_filled
)