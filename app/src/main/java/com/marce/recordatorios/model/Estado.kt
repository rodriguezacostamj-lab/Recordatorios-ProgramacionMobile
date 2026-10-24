package com.marce.recordatorios.model

import java.time.LocalDate

sealed class Estado(val etiqueta: String) {

    data object Pendiente : Estado("Pendiente")

    data class Completado(val fecha: LocalDate) : Estado("Completado") {
        init {
            require(!fecha.isAfter(LocalDate.now())) {
                "La fecha de completado no puede ser futura"
            }
        }
    }
}

