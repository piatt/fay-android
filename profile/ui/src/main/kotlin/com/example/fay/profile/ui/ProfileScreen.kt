package com.example.fay.profile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fay.core.ui.components.CenteredCallout

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CenteredCallout(
            image = R.drawable.ic_user,
            messageResId = R.string.coming_soon_label,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp)
        ) {
            Text(stringResource(com.example.fay.auth.ui.R.string.logout_label))
        }
    }
}