package com.example.fay.journal.ui

import com.example.fay.core.navigation.BottomNavItem

class JournalNavItem : BottomNavItem(
    appRoute = JournalRoute,
    label = R.string.journal_label,
    icon = R.drawable.ic_journal,
    selectedIcon = R.drawable.ic_journal_filled
)