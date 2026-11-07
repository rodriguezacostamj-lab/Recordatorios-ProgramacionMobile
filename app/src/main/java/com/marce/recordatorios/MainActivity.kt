package com.marce.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marce.recordatorios.ui.navegacion.NavegacionApp
import com.marce.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordatoriosTheme {
                NavegacionApp()
            }
        }
    }
}