package com.example.fay.journal.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fay.core.ui.components.CenteredCallout

@Composable
fun JournalScreen(modifier: Modifier = Modifier) {
    CenteredCallout(
        image = R.drawable.ic_journal,
        messageResId = R.string.coming_soon_label,
        modifier = modifier
    )
}