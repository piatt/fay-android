package com.example.fay.chat.ui

import com.example.fay.core.navigation.BottomNavItem

class ChatNavItem : BottomNavItem(
    appRoute = ChatRoute,
    label = R.string.chat_label,
    icon = R.drawable.ic_chat,
    selectedIcon = R.drawable.ic_chat_filled
)