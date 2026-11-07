package com.marce.recordatorios.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marce.recordatorios.ui.detalle.DetalleScreen
import com.marce.recordatorios.ui.formulario.FormularioScreen
import com.marce.recordatorios.ui.lista.ListaScreen

/** Todas las rutas de la app en un solo lugar. */
object Rutas {
    const val LISTA = "lista"
    const val CREAR = "crear"
    const val ARG_ID = "id"
    const val DETALLE = "detalle/{$ARG_ID}"

    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun NavegacionApp(
    navController: NavHostController = rememberNavController(),
) {
    // Vuelve al listado sin crear una copia nueva y sin pasarse de largo
    val volverAlListado: () -> Unit = {
        navController.popBackStack(Rutas.LISTA, inclusive = false)
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTA,
    ) {
        // ---------- Destino inicial: listado ----------
        composable(Rutas.LISTA) {
            ListaScreen(
                onCrearRecordatorio = {
                    navController.navigate(Rutas.CREAR) { launchSingleTop = true }
                },
                onRecordatorioClick = { id ->
                    navController.navigate(Rutas.detalle(id)) { launchSingleTop = true }
                },
            )
        }

        // ---------- Crear recordatorio ----------
        composable(Rutas.CREAR) {
            FormularioScreen(
                onGuardar = volverAlListado,
                onCancelar = volverAlListado,
            )
        }

        // ---------- Detalle: recibe SOLO el id ----------
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(Rutas.ARG_ID) ?: return@composable
            DetalleScreen(
                id = id,
                onVolver = volverAlListado,
            )
        }
    }
}

