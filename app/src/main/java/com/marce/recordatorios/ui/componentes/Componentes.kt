package com.marce.recordatorios.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.marce.recordatorios.data.DatosSimulados
import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Estado
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaRecordatorio(
    recordatorio: Recordatorio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorCategoria = colorCategoria(recordatorio.categoria)

    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorCategoria)
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (recordatorio.estaCompletado) TextDecoration.LineThrough else null,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Etiqueta(recordatorio.categoria.nombre, colorCategoria)
                    Etiqueta("Prioridad ${recordatorio.prioridad.etiqueta}", colorPrioridad(recordatorio.prioridad))
                    Etiqueta(recordatorio.estado.etiqueta, colorEstado(recordatorio.estado))
                }
            }
        }
    }
}


@Composable
fun Etiqueta(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}


@Composable
fun MensajeEstado(
    mensaje: String,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    textoAccion: String? = null,
    onAccion: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (cargando) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
        }
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (textoAccion != null && onAccion != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAccion) {
                Text(textoAccion)
            }
        }
    }
}


@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    textoAyuda: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        isError = error != null,
        singleLine = singleLine,
        minLines = minLines,
        supportingText = if (error != null || textoAyuda != null) {
            {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // El "⚠" y el texto hacen que el error no dependa solo del color
                    Text(
                        text = error?.let { "⚠ $it" } ?: "",
                        modifier = Modifier.weight(1f),
                    )
                    if (textoAyuda != null) Text(textoAyuda)
                }
            }
        } else null,
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (error != null) error(error) },   // lectores de pantalla
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorOpciones(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    textoOpcion: (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    Column(
        modifier = modifier.semantics { if (error != null) error(error) },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccionada,
                    onClick = { onSeleccion(opcion) },
                    label = { Text(textoOpcion(opcion)) },
                )
            }
        }
        if (error != null) {
            Text(
                text = "⚠ $error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}


fun colorCategoria(categoria: Categoria): Color =
    DatosSimulados.coloresPorCategoria[categoria] ?: Color.Gray

fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.Alta -> Color(0xFFD32F2F)
    Prioridad.Media -> Color(0xFFF57C00)
    Prioridad.Baja -> Color(0xFF616161)
}

fun colorEstado(estado: Estado): Color = when (estado) {
    Estado.Pendiente -> Color(0xFF1565C0)
    is Estado.Completado -> Color(0xFF2E7D32)
}