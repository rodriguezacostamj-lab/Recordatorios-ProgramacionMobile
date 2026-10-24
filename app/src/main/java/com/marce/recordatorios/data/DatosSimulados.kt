package com.marce.recordatorios.data

import androidx.compose.ui.graphics.Color
import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Estado
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio
import java.time.LocalDate

object DatosSimulados {

    private val hoy: LocalDate = LocalDate.now()


    val recordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 1,
            titulo = "Estudiar Kotlin",
            categoria = Categoria.Estudio,
            prioridad = Prioridad.Alta,
            descripcion = "Data classes y sealed classes",
            fechaLimite = hoy.plusDays(2),
        ),
        Recordatorio(
            id = 2,
            titulo = "Entregar Actividad 1",
            categoria = Categoria.Estudio,
            prioridad = Prioridad.Media,
            fechaLimite = hoy.plusDays(5),
        ),
        Recordatorio(
            id = 3,
            titulo = "Revisar apuntes",
            categoria = Categoria.Personal,
            prioridad = Prioridad.Baja,
            estado = Estado.Completado(hoy.minusDays(1)),
        ),
        Recordatorio(
            id = 4,
            titulo = "Reunión de equipo",
            categoria = Categoria.Trabajo,
            prioridad = Prioridad.Media,
            descripcion = "Planificación semanal",
        ),
        Recordatorio(
            id = 5,
            titulo = "Turno con el médico",
            categoria = Categoria.Salud,
            prioridad = Prioridad.Alta,
            fechaLimite = hoy.plusDays(7),
        ),
        Recordatorio(
            id = 6,
            titulo = "Responder mails",
            categoria = Categoria.Trabajo,
            prioridad = Prioridad.Baja,
            estado = Estado.Completado(hoy),
        ),
    )


    val categoriasUsadas: Set<Categoria> =
        recordatorios.map { it.categoria }.toSet()

    // MAP: cada categoría asociada a un color
    val coloresPorCategoria: Map<Categoria, Color> = mapOf(
        Categoria.Estudio to Color(0xFF1E88E5),
        Categoria.Trabajo to Color(0xFF8E24AA),
        Categoria.Personal to Color(0xFF43A047),
        Categoria.Salud to Color(0xFFE53935),
    )

    // FILTROS por estado y prioridad
    fun pendientes(lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.filter { it.estado is Estado.Pendiente }

    fun completados(lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.filter { it.estado is Estado.Completado }

    fun porPrioridad(
        prioridad: Prioridad,
        lista: List<Recordatorio> = recordatorios,
    ): List<Recordatorio> =
        lista.filter { it.prioridad == prioridad }

    fun ordenadosPorPrioridad(lista: List<Recordatorio> = recordatorios): List<Recordatorio> =
        lista.sortedByDescending { it.prioridad.nivel }
}
