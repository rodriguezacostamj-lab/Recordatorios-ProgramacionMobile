package com.marce.recordatorios.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.marce.recordatorios.data.ContenedorApp
import com.marce.recordatorios.data.ModoSimulacion
import com.marce.recordatorios.data.RecordatorioRepository
import com.marce.recordatorios.data.Simulacion
import com.marce.recordatorios.model.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface ListaUiState {
    data object Cargando : ListaUiState
    data object Vacio : ListaUiState
    data class ConDatos(val recordatorios: List<Recordatorio>) : ListaUiState
    data class Error(val mensaje: String) : ListaUiState
}

class ListaViewModel(
    private val repositorio: RecordatorioRepository,   // depende de la INTERFAZ
) : ViewModel() {


    private val _uiState = MutableStateFlow<ListaUiState>(ListaUiState.Cargando)
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    private val _modoSimulacion = MutableStateFlow(Simulacion.modo)
    val modoSimulacion: StateFlow<ModoSimulacion> = _modoSimulacion.asStateFlow()

    private var cargaJob: Job? = null

    fun cargar() {
        cargaJob?.cancel()   // si había una carga en curso, se cancela
        cargaJob = viewModelScope.launch {
            _uiState.update { ListaUiState.Cargando }
            val nuevoEstado = try {
                val lista = repositorio.obtenerTodos()
                if (lista.isEmpty()) {
                    ListaUiState.Vacio
                } else {
                    ListaUiState.ConDatos(lista.sortedByDescending { it.prioridad.nivel })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ListaUiState.Error(e.message ?: "Ocurrió un error inesperado.")
            }
            _uiState.update { nuevoEstado }
        }
    }

    fun onReintentar() = cargar()

    fun onModoSimulacionChange(modo: ModoSimulacion) {
        Simulacion.modo = modo
        _modoSimulacion.update { modo }
        cargar()
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ListaViewModel(ContenedorApp.repositorio) }
        }
    }
}

