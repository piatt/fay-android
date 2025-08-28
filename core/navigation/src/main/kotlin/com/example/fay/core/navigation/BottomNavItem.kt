package com.example.fay.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class BottomNavItem(
    val screen: Screen,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
)