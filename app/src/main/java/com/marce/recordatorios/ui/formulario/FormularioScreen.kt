package com.marce.recordatorios.ui.formulario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marce.recordatorios.model.Categoria
import com.marce.recordatorios.model.Prioridad
import com.marce.recordatorios.model.Recordatorio
import com.marce.recordatorios.ui.componentes.CampoTexto
import com.marce.recordatorios.ui.componentes.SelectorOpciones
import com.marce.recordatorios.ui.theme.RecordatoriosTheme

// =====================================================================
// PANTALLA CON ESTADO
// =====================================================================
@Composable
fun FormularioScreen(
    onGuardar: () -> Unit,     // se llama cuando el guardado terminó con éxito
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FormularioViewModel = viewModel(factory = FormularioViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el ViewModel informa éxito, la pantalla avisa hacia afuera (navegación)
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) onGuardar()
    }

    FormularioContenido(
        uiState = uiState,
        onTituloChange = viewModel::onTituloChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onCategoriaSeleccionada = viewModel::onCategoriaSeleccionada,
        onPrioridadSeleccionada = viewModel::onPrioridadSeleccionada,
        onGuardarClick = viewModel::onGuardar,
        onCancelar = onCancelar,
        modifier = modifier,
    )
}


// PANTALLA SIN ESTADO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContenido(
    uiState: FormularioUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaSeleccionada: (Categoria) -> Unit,
    onPrioridadSeleccionada: (Prioridad) -> Unit,
    onGuardarClick: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo recordatorio") },
                navigationIcon = {
                    TextButton(onClick = onCancelar, enabled = !uiState.enviando) {
                        Text("✕")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CampoTexto(
                valor = uiState.titulo,
                onValorChange = onTituloChange,
                etiqueta = "Título *",
                error = uiState.errorTitulo,
                textoAyuda = "${uiState.titulo.trim().length}/${Recordatorio.MAX_TITULO}",
            )

            CampoTexto(
                valor = uiState.descripcion,
                onValorChange = onDescripcionChange,
                etiqueta = "Descripción (opcional)",
                error = uiState.errorDescripcion,
                textoAyuda = "${uiState.descripcion.length}/${ValidadorFormulario.MAX_DESCRIPCION}",
                singleLine = false,
                minLines = 3,
            )

            SelectorOpciones(
                titulo = "Categoría *",
                opciones = ValidadorFormulario.CATEGORIAS,
                seleccionada = uiState.categoria,
                textoOpcion = { it.nombre },
                onSeleccion = onCategoriaSeleccionada,
                error = uiState.errorCategoria,
            )

            SelectorOpciones(
                titulo = "Prioridad *",
                opciones = ValidadorFormulario.PRIORIDADES,
                seleccionada = uiState.prioridad,
                textoOpcion = { it.etiqueta },
                onSeleccion = onPrioridadSeleccionada,
                error = uiState.errorPrioridad,
            )

            // Error general de guardado
            uiState.errorGeneral?.let { mensaje ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Text(
                        text = "⚠ $mensaje",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .padding(16.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !uiState.enviando,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = onGuardarClick,
                    enabled = uiState.puedeGuardar,   // deshabilitado si es inválido o está enviando
                    modifier = Modifier.weight(1f),
                ) {
                    if (uiState.enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando…")
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FormularioContenidoPreview() {
    RecordatoriosTheme {
        FormularioContenido(
            uiState = FormularioUiState(
                titulo = "Es",
                errorTitulo = ValidadorFormulario.validarTitulo("Es"),
                categoria = Categoria.Estudio,
            ),
            onTituloChange = {},
            onDescripcionChange = {},
            onCategoriaSeleccionada = {},
            onPrioridadSeleccionada = {},
            onGuardarClick = {},
            onCancelar = {},
        )
    }
}

