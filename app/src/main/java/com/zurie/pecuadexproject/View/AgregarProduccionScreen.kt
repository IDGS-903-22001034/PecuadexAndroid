package com.zurie.pecuadexproject.UI.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Producto
import com.zurie.pecuadexproject.Data.Model.ProductoEspacio
import com.zurie.pecuadexproject.ViewModels.ProductoEspacioViewModel
import com.zurie.pecuadexproject.ViewModels.ProductoViewModel
import com.zurie.pecuadexproject.ViewModels.EspacioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProduccionScreen(
    espacioId: Long,
    productoViewModel: ProductoViewModel = viewModel(),
    productoEspacioViewModel: ProductoEspacioViewModel = viewModel(),
    espacioViewModel: EspacioViewModel = viewModel(),
    navController: NavHostController? = null
) {
    // Estados
    val estadoEspacio = espacioViewModel.state
    val estadoProductos = productoViewModel.state
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var showProductoDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Cargar datos
    LaunchedEffect(espacioId) {
        espacioViewModel.obtenerEspacioId(espacioId)
        productoViewModel.obtenerProductos()
    }

    // Manejo de estados de carga
    if (estadoEspacio.isLoading || estadoProductos.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Manejo de errores
    estadoEspacio.error?.let { error ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error al cargar espacio: $error", color = Color.Red)
        }
        return
    }

    estadoProductos.error?.let { error ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error al cargar productos: $error", color = Color.Red)
        }
        return
    }

    val espacio = estadoEspacio.espacioSeleccionado ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Espacio no encontrado", color = Color.Red)
        }
        return
    }

    // UI Principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Información del espacio
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Espacio:", style = MaterialTheme.typography.labelMedium)
                    Text(espacio.nombre, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de producto
            OutlinedButton(
                onClick = { showProductoDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(productoSeleccionado?.nombre ?: "Seleccionar producto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para agregar
            Button(
                onClick = {
                    when {
                        productoSeleccionado == null -> {
                            errorMessage = "Debes seleccionar un producto"
                            showErrorDialog = true
                        }
                        else -> {
                            val productoEspacio = ProductoEspacio(
                                productoId = productoSeleccionado!!.idProducto?.toLong() ?: 0,
                                espacioId = espacio.idEspacio
                            )
                            productoEspacioViewModel.agregarProductoEspacio(
                                productoEspacio,
                                onSuccess = { showSuccessDialog = true },
                                onError = { error ->
                                    errorMessage = error
                                    showErrorDialog = true
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Producto")
            }
        }
    }

    // Diálogo para seleccionar producto
    if (showProductoDialog) {
        AlertDialog(
            onDismissRequest = { showProductoDialog = false },
            title = { Text("Seleccionar Producto") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(estadoProductos.productos) { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    productoSeleccionado = producto
                                    showProductoDialog = false
                                }
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Text(
                                text = producto.nombre,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProductoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController?.popBackStack()
            },
            title = { Text("Éxito") },
            text = { Text("Producto agregado correctamente") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController?.popBackStack()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error", color = Color.Red) },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}