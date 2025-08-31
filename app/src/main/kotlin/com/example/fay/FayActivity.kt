package com.example.fay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fay.components.FayApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FayActivity : ComponentActivity() {
    private val viewModel: FayAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            splashScreen.setKeepOnScreenCondition { state.loading }
            FayApp(
                state = state,
                onLogout = viewModel::logout
            )
        }
    }
}