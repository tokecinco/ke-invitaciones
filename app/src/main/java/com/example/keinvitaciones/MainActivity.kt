package com.example.keinvitaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.keinvitaciones.ui.screens.MainContainerScreen
import com.example.keinvitaciones.ui.theme.KEInvitacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KEInvitacionesTheme {
                MainContainerScreen()
            }
        }
    }
}
