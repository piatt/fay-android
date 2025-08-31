package com.example.fay.chat.ui

import com.example.fay.core.navigation.BottomNavItem

class ChatNavItem : BottomNavItem(
    appRoute = ChatRoute,
    labelResId = R.string.chat_label,
    iconResId = R.drawable.ic_chat,
    selectedIconResId = R.drawable.ic_chat_filled
)