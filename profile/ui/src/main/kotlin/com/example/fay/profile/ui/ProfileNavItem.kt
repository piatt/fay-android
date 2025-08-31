package com.example.fay.profile.ui

import com.example.fay.core.navigation.BottomNavItem

class ProfileNavItem : BottomNavItem(
    appRoute = ProfileRoute,
    labelResId = R.string.profile_label,
    iconResId = R.drawable.ic_user,
    selectedIconResId = R.drawable.ic_user_filled
)