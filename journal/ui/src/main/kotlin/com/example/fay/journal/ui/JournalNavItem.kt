package com.example.fay.journal.ui

import com.example.fay.core.navigation.BottomNavItem

class JournalNavItem : BottomNavItem(
    appRoute = JournalRoute,
    labelResId = R.string.journal_label,
    iconResId = R.drawable.ic_journal,
    selectedIconResId = R.drawable.ic_journal_filled
)