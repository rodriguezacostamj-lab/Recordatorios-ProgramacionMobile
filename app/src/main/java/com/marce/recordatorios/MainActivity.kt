package com.marce.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.marce.recordatorios.data.DatosSimulados
import com.marce.recordatorios.ui.PantallaRecordatorios
import com.marce.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordatoriosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRecordatorios(
                        recordatorios = DatosSimulados.ordenadosPorPrioridad(),
                        categorias = DatosSimulados.categoriasUsadas,
                        colores = DatosSimulados.coloresPorCategoria,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}