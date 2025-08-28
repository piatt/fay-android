package com.example.fay.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fay.core.ui.components.CenteredCallout

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    CenteredCallout(
        image = R.drawable.ic_chat,
        message = R.string.coming_soon_label,
        modifier = modifier
    )
}