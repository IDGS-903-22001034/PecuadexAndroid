package com.zurie.pecuadexproject.View

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.zurie.pecuadexproject.UI.Screens.AgregarProduccionScreen
import com.zurie.pecuadexproject.UI.Screens.EspacioScreen
import com.zurie.pecuadexproject.ViewModels.MainViewModel

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("principal") { PrincipalScreen(navController) }
        composable("listaEspacios") {
            ListaEspaciosScreen(navController)
        }

        composable(
            "agregarAnimal/{espacioId}",
            arguments = listOf(navArgument("espacioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val espacioId = backStackEntry.arguments?.getLong("espacioId") ?: 0L
            AgregarAnimalScreen(
                espacioId = espacioId,
                navController = navController
            )
        }

        composable(
            "editarAnimal/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId") ?: 0L
            EditarAnimalScreen(
                animalId = animalId,
                navController = navController
            )
        }
        composable(
            "detalleEspacio/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            val id = it.arguments?.getLong("id") ?: 0L
            DetalleEspacioScreen(id, navController)
        }
        composable("espacios") {
            EspacioScreen(navController = navController)
        }

        composable(
            "agregarProduccion/{espacioId}",
            arguments = listOf(navArgument("espacioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val espacioId = backStackEntry.arguments?.getLong("espacioId") ?: 0L
            AgregarProduccionScreen(
                espacioId = espacioId,
                navController = navController
            )
        }

        composable(
            "editarEspacio/{espacioId}",
            arguments = listOf(navArgument("espacioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val espacioId = backStackEntry.arguments?.getLong("espacioId") ?: 0L
            EditarEspacioScreen(
                espacioId = espacioId,
                navController = navController
            )
        }

        composable("mapa") {
            MapScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("alertas") {
            AlertsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }

}
