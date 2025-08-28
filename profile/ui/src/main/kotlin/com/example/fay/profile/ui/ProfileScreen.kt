package com.example.fay.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fay.core.ui.components.CenteredCallout

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    CenteredCallout(
        image = R.drawable.ic_user,
        message = R.string.coming_soon_label,
        modifier = modifier
    )
}