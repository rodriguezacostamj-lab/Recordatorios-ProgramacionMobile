package com.marce.recordatorios.data

import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio



interface RecordatorioRepository {

    suspend fun obtenerTodos(): List<Recordatorio>

    suspend fun obtenerPorId(id: Int): Recordatorio?

    suspend fun guardar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria,
        prioridad: Prioridad,
    ): Recordatorio
}