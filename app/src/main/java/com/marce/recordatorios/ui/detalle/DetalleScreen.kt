package com.marce.recordatorios.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marce.recordatorios.data.DatosSimulados
import com.marce.recordatorios.model.Estado
import com.marce.recordatorios.model.Recordatorio
import com.marce.recordatorios.ui.componentes.Etiqueta
import com.marce.recordatorios.ui.componentes.MensajeEstado
import com.marce.recordatorios.ui.componentes.colorCategoria
import com.marce.recordatorios.ui.componentes.colorEstado
import com.marce.recordatorios.ui.componentes.colorPrioridad
import com.marce.recordatorios.ui.theme.RecordatoriosTheme
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")


// PANTALLA CON ESTADO

@Composable
fun DetalleScreen(
    id: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleViewModel = viewModel(factory = DetalleViewModel.factory(id)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetalleContenido(
        uiState = uiState,
        onVolver = onVolver,
        onReintentar = viewModel::onReintentar,
        modifier = modifier,
    )
}


// PANTALLA SIN ESTADO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContenido(
    uiState: DetalleUiState,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("←") }
                },
            )
        },
    ) { innerPadding ->
        val contenidoModifier = Modifier.padding(innerPadding)

        when (uiState) {
            DetalleUiState.Cargando -> MensajeEstado(
                mensaje = "Cargando recordatorio…",
                modifier = contenidoModifier,
                cargando = true,
            )

            is DetalleUiState.Error -> MensajeEstado(
                mensaje = uiState.mensaje,
                modifier = contenidoModifier,
                textoAccion = "Reintentar",
                onAccion = onReintentar,
            )

            is DetalleUiState.Encontrado -> DetalleRecordatorio(
                recordatorio = uiState.recordatorio,
                onVolver = onVolver,
                modifier = contenidoModifier,
            )
        }
    }
}


// INFORMACIÓN COMPLETA DEL RECORDATORIO

@Composable
fun DetalleRecordatorio(
    recordatorio: Recordatorio,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = recordatorio.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Etiqueta(recordatorio.categoria.nombre, colorCategoria(recordatorio.categoria))
            Etiqueta("Prioridad ${recordatorio.prioridad.etiqueta}", colorPrioridad(recordatorio.prioridad))
            Etiqueta(recordatorio.estado.etiqueta, colorEstado(recordatorio.estado))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilaDato("Descripción", recordatorio.descripcion ?: "Sin descripción")
                FilaDato("Categoría", recordatorio.categoria.nombre)
                FilaDato("Prioridad", recordatorio.prioridad.etiqueta)
                FilaDato(
                    "Estado",
                    when (val estado = recordatorio.estado) {
                        Estado.Pendiente -> "Pendiente"
                        is Estado.Completado -> "Completado el ${estado.fecha.format(formatoFecha)}"
                    },
                )
                FilaDato(
                    "Fecha límite",
                    recordatorio.fechaLimite?.format(formatoFecha) ?: "Sin fecha límite",
                )
                FilaDato("Identificador", "#${recordatorio.id}")
            }
        }

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al listado")
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}


@Preview(showBackground = true)
@Composable
fun DetalleContenidoPreview() {
    RecordatoriosTheme {
        DetalleContenido(
            uiState = DetalleUiState.Encontrado(DatosSimulados.recordatorios.first()),
            onVolver = {},
            onReintentar = {},
        )
    }
}

