package com.marce.recordatorios.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.marce.recordatorios.data.ContenedorApp
import com.marce.recordatorios.data.RecordatorioRepository
import com.marce.recordatorios.model.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Encontrado(val recordatorio: Recordatorio) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

class DetalleViewModel(
    private val repositorio: RecordatorioRepository,
    private val id: Int,                       // solo recibe el identificador
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { DetalleUiState.Cargando }
            val nuevoEstado = try {
                val recordatorio = repositorio.obtenerPorId(id)
                if (recordatorio != null) {
                    DetalleUiState.Encontrado(recordatorio)
                } else {
                    DetalleUiState.Error("No se encontró el recordatorio #$id.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DetalleUiState.Error(e.message ?: "No se pudo cargar el recordatorio.")
            }
            _uiState.update { nuevoEstado }
        }
    }

    fun onReintentar() = cargar()

    companion object {
        /** Como necesita el id, la fábrica es una función que lo recibe. */
        fun factory(id: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetalleViewModel(ContenedorApp.repositorio, id) }
        }
    }
}

