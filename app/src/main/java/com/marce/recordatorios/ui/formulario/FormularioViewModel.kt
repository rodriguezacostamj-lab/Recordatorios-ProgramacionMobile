package com.marce.recordatorios.ui.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.marce.recordatorios.data.ContenedorApp
import com.marce.recordatorios.data.RecordatorioRepository
import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// REGLAS DE VALIDACIÓN DEL FORMULARIO

object ValidadorFormulario {
    const val MIN_TITULO = 4
    const val MAX_DESCRIPCION = 200

    val CATEGORIAS: List<Categoria> = listOf(
        Categoria.Estudio, Categoria.Trabajo, Categoria.Personal, Categoria.Salud,
    )
    val PRIORIDADES: List<Prioridad> = listOf(
        Prioridad.Alta, Prioridad.Media, Prioridad.Baja,
    )

    fun validarTitulo(titulo: String): String? {
        val limpio = titulo.trim()
        return when {
            limpio.length < MIN_TITULO ->
                "El título debe tener al menos $MIN_TITULO caracteres"
            limpio.length > Recordatorio.MAX_TITULO ->
                "El título no puede superar los ${Recordatorio.MAX_TITULO} caracteres"
            else -> null
        }
    }

    fun validarDescripcion(descripcion: String): String? =
        if (descripcion.length > MAX_DESCRIPCION) {
            "La descripción no puede superar los $MAX_DESCRIPCION caracteres"
        } else null

    fun validarCategoria(categoria: Categoria?): String? = when {
        categoria == null -> "Seleccioná una categoría"
        categoria !in CATEGORIAS -> "La categoría no es válida"
        else -> null
    }

    fun validarPrioridad(prioridad: Prioridad?): String? =
        if (prioridad == null) "Seleccioná una prioridad" else null

    fun esValido(titulo: String, descripcion: String, categoria: Categoria?, prioridad: Prioridad?): Boolean =
        validarTitulo(titulo) == null &&
                validarDescripcion(descripcion) == null &&
                validarCategoria(categoria) == null &&
                validarPrioridad(prioridad) == null
}


data class FormularioUiState(
    // Valores actuales de los campos
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    // Mensajes de validación
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null,
    // Envío
    val enviando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null,
) {
    val puedeGuardar: Boolean
        get() = !enviando &&
                ValidadorFormulario.esValido(titulo, descripcion, categoria, prioridad)
}


class FormularioViewModel(
    private val repositorio: RecordatorioRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    // ----- Eventos que llegan desde la interfaz -----
    fun onTituloChange(valor: String) = _uiState.update {
        it.copy(titulo = valor, errorTitulo = ValidadorFormulario.validarTitulo(valor), errorGeneral = null)
    }

    fun onDescripcionChange(valor: String) = _uiState.update {
        it.copy(descripcion = valor, errorDescripcion = ValidadorFormulario.validarDescripcion(valor), errorGeneral = null)
    }

    fun onCategoriaSeleccionada(categoria: Categoria) = _uiState.update {
        it.copy(categoria = categoria, errorCategoria = ValidadorFormulario.validarCategoria(categoria))
    }

    fun onPrioridadSeleccionada(prioridad: Prioridad) = _uiState.update {
        it.copy(prioridad = prioridad, errorPrioridad = ValidadorFormulario.validarPrioridad(prioridad))
    }

    fun onGuardar() {
        val actual = _uiState.value
        if (!actual.puedeGuardar) return               // doble protección
        val categoria = actual.categoria ?: return
        val prioridad = actual.prioridad ?: return

        _uiState.update { it.copy(enviando = true, errorGeneral = null) }

        viewModelScope.launch {
            try {
                repositorio.guardar(
                    titulo = actual.titulo.trim(),
                    descripcion = actual.descripcion.trim().ifBlank { null },
                    categoria = categoria,
                    prioridad = prioridad,
                )
                _uiState.update { it.copy(enviando = false, guardadoExitoso = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(enviando = false, errorGeneral = e.message ?: "No se pudo guardar.")
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { FormularioViewModel(ContenedorApp.repositorio) }
        }
    }
}

