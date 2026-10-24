package com.marce.recordatorios.model

import java.time.LocalDate

data class Recordatorio(
    val id: Int,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: Estado = Estado.Pendiente,
    val descripcion: String? = null,
    val fechaLimite: LocalDate? = null,
) {
    init {
        require(id > 0) { "El id debe ser positivo" }
        require(titulo.isNotBlank()) { "El título no puede estar vacío" }
        require(titulo.length <= MAX_TITULO) {
            "El título no puede superar los $MAX_TITULO caracteres"
        }
        require(descripcion == null || descripcion.isNotBlank()) {
            "Si hay descripción, no puede estar vacía"
        }
    }

    val estaCompletado: Boolean
        get() = estado is Estado.Completado

    fun completar(fecha: LocalDate = LocalDate.now()): Recordatorio =
        copy(estado = Estado.Completado(fecha))

    companion object {
        const val MAX_TITULO = 50
    }
}

