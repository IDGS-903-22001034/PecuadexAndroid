package com.zurie.pecuadexproject.UI.Screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspacioScreen(
    espacioViewModel: EspacioViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel(),
    navController: NavHostController? = null
) {
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var razaSeleccionada by remember { mutableStateOf<Raza?>(null) }
    var showRazaDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val estadoRaza = razaViewModel.state

    LaunchedEffect(Unit) {
        if (estadoRaza.razas.isEmpty()) {
            razaViewModel.obtenerRazas()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Espacio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
                        },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController?.popBackStack()
                    },
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

                Button(
                    onClick = {
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
                                val nuevoEspacio = Espacio(
                                    nombre = nombre,
                                    direccion = direccion,
                                    estatus = true,
                                    razaId = razaSeleccionada!!.idRaza
                                )
                                espacioViewModel.guardarEspacio(
                                    nuevoEspacio,
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
                    )
                ) {
                    Text("Guardar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

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
                    if (estadoRaza.razas.isEmpty()) {
                        Text(
                            "No hay razas disponibles",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        estadoRaza.razas.forEach { raza ->
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

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController?.popBackStack()
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
                Text("El espacio se ha creado correctamente")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController?.popBackStack()
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
}
