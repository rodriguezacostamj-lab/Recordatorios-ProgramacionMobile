package com.marce.recordatorios.data

import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RecordatorioRepositoryEnMemoria(
    datosIniciales: List<Recordatorio> = DatosSimulados.recordatorios,
) : RecordatorioRepository {

    private val mutex = Mutex()
    private var recordatorios: List<Recordatorio> = datosIniciales

    override suspend fun obtenerTodos(): List<Recordatorio> {
        delay(Simulacion.DEMORA_MS)
        return when (Simulacion.modo) {
            ModoSimulacion.VACIO -> emptyList()
            ModoSimulacion.ERROR_CARGA -> throw IllegalStateException(
                "No se pudieron cargar los recordatorios. Revisá la conexión e intentá de nuevo."
            )
            else -> mutex.withLock { recordatorios }
        }
    }

    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        delay(500)
        return mutex.withLock { recordatorios.find { it.id == id } }
    }

    override suspend fun guardar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria,
        prioridad: Prioridad,
    ): Recordatorio {
        delay(Simulacion.DEMORA_MS)
        if (Simulacion.modo == ModoSimulacion.ERROR_GUARDADO) {
            throw IllegalStateException("No se pudo guardar el recordatorio. Intentá nuevamente.")
        }
        return mutex.withLock {
            val nuevo = Recordatorio(
                id = (recordatorios.maxOfOrNull { it.id } ?: 0) + 1,
                titulo = titulo,
                categoria = categoria,
                prioridad = prioridad,
                descripcion = descripcion,
            )
            recordatorios = recordatorios + nuevo   // nueva lista, no se modifica la anterior
            nuevo
        }
    }
}


object ContenedorApp {
    val repositorio: RecordatorioRepository by lazy { RecordatorioRepositoryEnMemoria() }
}
