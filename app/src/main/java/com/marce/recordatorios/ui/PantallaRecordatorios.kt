package com.marce.recordatorios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marce.recordatorios.data.DatosSimulados
import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Estado
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio
import com.marce.recordatorios.ui.theme.RecordatoriosTheme


@Composable
fun PantallaRecordatorios(
    recordatorios: List<Recordatorio>,
    categorias: Set<Categoria>,
    colores: Map<Categoria, Color>,
    modifier: Modifier = Modifier,
) {
    val cantidadPendientes = DatosSimulados.pendientes(recordatorios).size

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        item {
            Column {
                Text(
                    text = "Mis recordatorios",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${recordatorios.size} en total · $cantidadPendientes pendientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }


        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categorias.toList()) { categoria ->
                    Etiqueta(
                        texto = categoria.nombre,
                        color = colores[categoria] ?: Color.Gray,
                    )
                }
            }
        }

        // Lista de recordatorios (viene de la List)
        items(recordatorios, key = { it.id }) { recordatorio ->
            TarjetaRecordatorio(
                recordatorio = recordatorio,
                colorCategoria = colores[recordatorio.categoria] ?: Color.Gray,
            )
        }
    }
}

// ---------- TARJETA DE UN RECORDATORIO ----------
@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio, colorCategoria: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Franja de color de la categoría (sale del Map)
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
                // 1. Título
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (recordatorio.estaCompletado) TextDecoration.LineThrough else null,
                )
                // Descripción opcional: solo se muestra si existe
                recordatorio.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // 2. Categoría · 3. Prioridad · 4. Estado
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Etiqueta(recordatorio.categoria.nombre, colorCategoria)
                    Etiqueta("Prioridad ${recordatorio.prioridad.etiqueta}", colorPrioridad(recordatorio.prioridad))
                    Etiqueta(recordatorio.estado.etiqueta, colorEstado(recordatorio.estado))
                }
            }
        }
    }
}

// ---------- ETIQUETA REUTILIZABLE ----------
@Composable
fun Etiqueta(texto: String, color: Color) {
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

// ---------- COLORES SEGÚN PRIORIDAD Y ESTADO ----------
// Como son sealed classes, el `when` no necesita `else`
private fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.Alta -> Color(0xFFD32F2F)
    Prioridad.Media -> Color(0xFFF57C00)
    Prioridad.Baja -> Color(0xFF616161)
}

private fun colorEstado(estado: Estado): Color = when (estado) {
    Estado.Pendiente -> Color(0xFF1565C0)
    is Estado.Completado -> Color(0xFF2E7D32)
}

// ---------- PREVIEW (para ver la pantalla sin ejecutar) ----------
@Preview(showBackground = true)
@Composable
fun PantallaRecordatoriosPreview() {
    RecordatoriosTheme {
        PantallaRecordatorios(
            recordatorios = DatosSimulados.ordenadosPorPrioridad(),
            categorias = DatosSimulados.categoriasUsadas,
            colores = DatosSimulados.coloresPorCategoria,
        )
    }
}

