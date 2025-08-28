package com.example.fay.chat.ui

import com.example.fay.core.navigation.BottomNavItem
import com.example.fay.core.navigation.Screen

class ChatNavItem : BottomNavItem(
    screen = Screen.Chat,
    label = R.string.chat_label,
    icon = R.drawable.ic_chat,
    selectedIcon = R.drawable.ic_chat_filled
)