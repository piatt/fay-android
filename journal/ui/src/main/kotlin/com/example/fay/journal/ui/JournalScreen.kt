package com.example.fay.journal.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.fay.core.ui.components.CenteredCallout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.journal_label))
                }
            )
        }
    ) { paddingValues ->
        CenteredCallout(
            image = R.drawable.ic_journal,
            messageResId = R.string.coming_soon_label,
            modifier = Modifier.padding(paddingValues)
        )
    }
}