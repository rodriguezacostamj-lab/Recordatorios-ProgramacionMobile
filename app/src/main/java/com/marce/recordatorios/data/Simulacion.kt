package com.marce.recordatorios.data

/** Modos para forzar cada estado de la interfaz sin base de datos ni servidor. */
enum class ModoSimulacion(val descripcion: String) {
    NORMAL("Datos normales"),
    VACIO("Lista vacía"),
    ERROR_CARGA("Error al cargar"),
    ERROR_GUARDADO("Error al guardar"),
}

object Simulacion {
    @Volatile
    var modo: ModoSimulacion = ModoSimulacion.NORMAL


    const val DEMORA_MS = 1500L
}


