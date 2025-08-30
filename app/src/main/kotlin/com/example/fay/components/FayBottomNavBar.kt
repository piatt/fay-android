package com.example.fay.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fay.appointments.ui.AppointmentsNavItem
import com.example.fay.chat.ui.ChatNavItem
import com.example.fay.journal.ui.JournalNavItem
import com.example.fay.profile.ui.ProfileNavItem

@Composable
fun FayBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navItems = listOf(
        AppointmentsNavItem(),
        ChatNavItem(),
        JournalNavItem(),
        ProfileNavItem()
    )
    NavigationBar {
        navItems.forEach { item ->
            val selected = navBackStackEntry?.destination?.hierarchy?.any {
                it.hasRoute(item.appRoute::class)
            } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selected) item.selectedIcon else item.icon
                        ),
                        contentDescription = stringResource(item.label)
                    )
                },
                label = {
                    Text(text = stringResource(item.label))
                },
                selected = selected,
                onClick = {
                    navController.navigate(route = item.appRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}