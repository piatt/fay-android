package com.example.fay.journal.ui

import com.example.fay.core.navigation.BottomNavItem
import com.example.fay.core.navigation.Screen

class JournalNavItem : BottomNavItem(
    screen = Screen.Journal,
    label = R.string.journal_label,
    icon = R.drawable.ic_journal,
    selectedIcon = R.drawable.ic_journal_filled
)