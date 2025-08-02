package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.ViewModels.EspacioViewModel
import com.zurie.pecuadexproject.ViewModels.RazaViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarEspacioScreen(
    espacioId: Long,
    navController: NavHostController,
    espacioViewModel: EspacioViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel()
) {
    // Estados para el formulario
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var razaSeleccionada by remember { mutableStateOf<Raza?>(null) }
    var showRazaDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val razas = razaViewModel.state.razas
    val espacioSeleccionado = espacioViewModel.state.espacioSeleccionado

    // Cargar datos del espacio
    LaunchedEffect(espacioId) {
        try {
            // Cargar datos en paralelo
            coroutineScope {
                launch { espacioViewModel.obtenerEspacioId(espacioId) }
                if (razas.isEmpty()) {
                    launch { razaViewModel.obtenerRazas() }
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar datos: ${e.message}"
            showErrorDialog = true
        } finally {
            isLoading = false
        }
    }

    // Actualizar campos cuando se cargue el espacio
    LaunchedEffect(espacioSeleccionado) {
        espacioSeleccionado?.let { espacio ->
            nombre = espacio.nombre
            direccion = espacio.direccion
            // Buscar la raza correspondiente en la lista de razas
            razaSeleccionada = razas.firstOrNull { it.idRaza == espacio.razaId }
        }
    }

    // Mostrar loading mientras se cargan los datos
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF4285F4))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Espacio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color(0xFF4285F4)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Nombre
            Text(
                "Nombre del espacio",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4285F4),
                    unfocusedBorderColor = Color(0xFFDADCE0)
                ),
                placeholder = { Text("Ej: Corral 1, Pastizal B") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Dirección
            Text(
                "Dirección",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4285F4),
                    unfocusedBorderColor = Color(0xFFDADCE0)
                ),
                placeholder = { Text("Ubicación exacta del espacio") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selección de Raza
            Text(
                "Raza asignada",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                onClick = { showRazaDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3C4043)
                ),
                border = BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Text(
                    razaSeleccionada?.nombre ?: "Selecciona una raza",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones Guardar, Eliminar y Cancelar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Eliminar
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDB4437),
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar")
                }

                // Botón Cancelar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5F6368)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFDADCE0))
                ) {
                    Text("Cancelar")
                }

                // Botón Guardar
                Button(
                    onClick = {
                        // Validación de campos
                        when {
                            nombre.isBlank() -> {
                                errorMessage = "Debes ingresar un nombre para el espacio"
                                showErrorDialog = true
                            }
                            direccion.isBlank() -> {
                                errorMessage = "Debes ingresar una dirección"
                                showErrorDialog = true
                            }
                            razaSeleccionada == null -> {
                                errorMessage = "Debes seleccionar una raza"
                                showErrorDialog = true
                            }
                            else -> {
                                val espacioActualizado = Espacio(
                                    idEspacio = espacioId,
                                    nombre = nombre,
                                    direccion = direccion,
                                    estatus = true, // Siempre activo
                                    razaId = razaSeleccionada!!.idRaza
                                )
                                espacioViewModel.modificarEspacio(
                                    espacioId,
                                    espacioActualizado,
                                    onSuccess = {
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        showErrorDialog = true
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4),
                        contentColor = Color.White
                    ),
                    enabled = !espacioViewModel.state.isLoading
                ) {
                    if (espacioViewModel.state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Diálogo para seleccionar raza
    if (showRazaDialog) {
        AlertDialog(
            onDismissRequest = { showRazaDialog = false },
            title = {
                Text(
                    "Seleccionar raza",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (razas.isEmpty()) {
                        Text(
                            "No hay razas disponibles",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        razas.forEach { raza ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        razaSeleccionada = raza
                                        showRazaDialog = false
                                    }
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (razaSeleccionada?.idRaza == raza.idRaza)
                                        Color(0xFFE8F0FE) else Color.White
                                )
                            ) {
                                Text(
                                    text = raza.nombre,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showRazaDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4285F4)
                    )
                ) {
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
                navController.popBackStack()
            },
            title = {
                Text(
                    "¡Éxito!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("El espacio se ha actualizado correctamente")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4)
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    "Error",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4285F4)
                    )
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Diálogo de confirmación para eliminar
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Confirmar eliminación",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar este espacio? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        espacioViewModel.eliminarEspacio(
                            espacioId,
                            onSuccess = {
                                // Redirigir a listaEspacios (sin "Screen" al final)
                                navController.navigate("listaEspacios") {
                                    // Limpiar el stack de navegación
                                    popUpTo("listaEspacios") { inclusive = true }
                                }
                            },
                            onError = { error ->
                                errorMessage = error
                                showErrorDialog = true
                                showDeleteDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDB4437)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF5F6368)
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}