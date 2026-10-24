package com.marce.recordatorios.model



sealed class Categoria(val nombre: String) {
    data object Estudio : Categoria("Estudio")
    data object Trabajo : Categoria("Trabajo")
    data object Personal : Categoria("Personal")
    data object Salud : Categoria("Salud")
}

