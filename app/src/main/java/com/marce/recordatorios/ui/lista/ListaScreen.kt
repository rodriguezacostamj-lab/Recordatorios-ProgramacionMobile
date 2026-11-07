package com.marce.recordatorios.ui.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marce.recordatorios.data.DatosSimulados
import com.marce.recordatorios.data.ModoSimulacion
import com.marce.recordatorios.ui.componentes.MensajeEstado
import com.marce.recordatorios.ui.componentes.TarjetaRecordatorio
import com.marce.recordatorios.ui.theme.RecordatoriosTheme


@Composable
fun ListaScreen(
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListaViewModel = viewModel(factory = ListaViewModel.Factory),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modoSimulacion by viewModel.modoSimulacion.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) { viewModel.cargar() }

    ListaContenido(
        uiState = uiState,
        modoSimulacion = modoSimulacion,
        onCrearRecordatorio = onCrearRecordatorio,
        onRecordatorioClick = onRecordatorioClick,
        onReintentar = viewModel::onReintentar,
        onModoSimulacionChange = viewModel::onModoSimulacionChange,
        modifier = modifier,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContenido(
    uiState: ListaUiState,
    modoSimulacion: ModoSimulacion,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    onReintentar: () -> Unit,
    onModoSimulacionChange: (ModoSimulacion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Mis recordatorios") },
                actions = {
                    MenuSimulacion(
                        modoActual = modoSimulacion,
                        onModoChange = onModoSimulacionChange,
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCrearRecordatorio) {
                Text("+ Nuevo")
            }
        },
    ) { innerPadding ->
        val contenidoModifier = Modifier.padding(innerPadding)


        when (uiState) {
            ListaUiState.Cargando -> MensajeEstado(
                mensaje = "Cargando recordatorios…",
                modifier = contenidoModifier,
                cargando = true,
            )

            ListaUiState.Vacio -> MensajeEstado(
                mensaje = "Todavía no tenés recordatorios.",
                modifier = contenidoModifier,
                textoAccion = "Crear recordatorio",
                onAccion = onCrearRecordatorio,
            )

            is ListaUiState.Error -> MensajeEstado(
                mensaje = uiState.mensaje,
                modifier = contenidoModifier,
                textoAccion = "Reintentar",
                onAccion = onReintentar,
            )

            is ListaUiState.ConDatos -> LazyColumn(
                modifier = contenidoModifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.recordatorios, key = { it.id }) { recordatorio ->
                    TarjetaRecordatorio(
                        recordatorio = recordatorio,
                        onClick = { onRecordatorioClick(recordatorio.id) },
                    )
                }
            }
        }
    }
}


@Composable
private fun MenuSimulacion(
    modoActual: ModoSimulacion,
    onModoChange: (ModoSimulacion) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }   // estado visual local del menú

    Box {
        TextButton(onClick = { expandido = true }) {
            Text("Simular")
        }
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
        ) {
            ModoSimulacion.entries.forEach { modo ->
                DropdownMenuItem(
                    text = { Text((if (modo == modoActual) "✓ " else "") + modo.descripcion) },
                    onClick = {
                        expandido = false
                        onModoChange(modo)
                    },
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ListaContenidoPreview() {
    RecordatoriosTheme {
        ListaContenido(
            uiState = ListaUiState.ConDatos(DatosSimulados.recordatorios),
            modoSimulacion = ModoSimulacion.NORMAL,
            onCrearRecordatorio = {},
            onRecordatorioClick = {},
            onReintentar = {},
            onModoSimulacionChange = {},
        )
    }
}

