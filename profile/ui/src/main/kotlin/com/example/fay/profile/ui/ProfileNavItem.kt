package com.example.fay.profile.ui

import com.example.fay.core.navigation.BottomNavItem
import com.example.fay.core.navigation.Screen

class ProfileNavItem : BottomNavItem(
    screen = Screen.Profile,
    label = R.string.profile_label,
    icon = R.drawable.ic_user,
    selectedIcon = R.drawable.ic_user_filled
)