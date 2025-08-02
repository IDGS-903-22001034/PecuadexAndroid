package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.ViewModels.RazaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarAnimalScreen(
    espacioId: Long,
    navController: NavHostController,
    animalViewModel: AnimalViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel()
) {
    // Estados para los campos del formulario
    var apodo by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") } // "M" o "H"
    var razaSeleccionada by remember { mutableStateOf<Raza?>(null) }
    var peso by remember { mutableStateOf<Int?>(null) }
    var pesoText by remember { mutableStateOf("") } // Para el TextField
    var fechaNacimiento by remember { mutableStateOf("") }

    // Estados para los diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showRazaDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val razas = razaViewModel.state.razas

    LaunchedEffect(Unit) {
        razaViewModel.obtenerRazas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nuevo Animal",
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

            // Campo Apodo
            Text(
                "Apodo del animal",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = apodo,
                onValueChange = { apodo = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4285F4),
                    unfocusedBorderColor = Color(0xFFDADCE0)
                ),
                placeholder = { Text("Ej: Lola, Torito") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selección de Sexo
            Text(
                "Sexo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Opciones de sexo con valores para el backend
                val opcionesSexo = listOf("M" to "Macho", "H" to "Hembra")

                opcionesSexo.forEach { (valor, etiqueta) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = (sexo == valor),
                                onClick = { sexo = valor }
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (sexo == valor),
                            onClick = { sexo = valor },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF4285F4)
                            )
                        )
                        Text(
                            etiqueta,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selección de Raza
            Text(
                "Raza",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Peso (manejado como Int)
            Text(
                "Peso (kg)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = pesoText,
                onValueChange = {
                    pesoText = it
                    peso = it.toIntOrNull() // Convierte a Int o null si no es número válido
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4285F4),
                    unfocusedBorderColor = Color(0xFFDADCE0)
                ),
                placeholder = { Text("Ej: 450") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Fecha de nacimiento con selector
            Text(
                "Fecha de nacimiento",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4285F4),
                    unfocusedBorderColor = Color(0xFFDADCE0)
                ),
                placeholder = { Text("dd/mm/aaaa") },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = Color(0xFF4285F4)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones Guardar y Cancelar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                Button(
                    onClick = {
                        // Validación mejorada con mensajes específicos
                        when {
                            apodo.isBlank() -> {
                                errorMessage = "Debes ingresar un apodo para el animal"
                                showErrorDialog = true
                            }
                            sexo.isEmpty() -> {
                                errorMessage = "Debes seleccionar el sexo del animal"
                                showErrorDialog = true
                            }
                            razaSeleccionada == null -> {
                                errorMessage = "Debes seleccionar una raza"
                                showErrorDialog = true
                            }
                            peso == null || peso!! <= 0 -> {
                                errorMessage = "Debes ingresar un peso válido (mayor a 0)"
                                showErrorDialog = true
                            }
                            fechaNacimiento.isBlank() -> {
                                errorMessage = "Debes seleccionar la fecha de nacimiento"
                                showErrorDialog = true
                            }
                            else -> {
                                try {
                                    val nuevoAnimal = Animal(
                                        apodo = apodo,
                                        sexo = sexo,
                                        razaId = razaSeleccionada!!.idRaza,
                                        espacioId = espacioId,
                                        peso = peso!!, // Usamos !! porque ya validamos que no es null
                                        fechaNacimiento = fechaNacimiento,
                                        critico = false
                                    )

                                    animalViewModel.agregarAnimal(
                                        nuevoAnimal,
                                        onSuccess = {
                                            showSuccessDialog = true
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorDialog = true
                                        }
                                    )
                                } catch (e: Exception) {
                                    errorMessage = "Error al crear el animal: ${e.message}"
                                    showErrorDialog = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4),
                        contentColor = Color.White
                    ),
                    enabled = !animalViewModel.state.isLoading
                ) {
                    if (animalViewModel.state.isLoading) {
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

    // Selector de fecha (usando DatePickerUtils)
    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            DatePickerDialog(
                onDateSelected = { date ->
                    fechaNacimiento = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
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
                Text("El animal se ha agregado correctamente")
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
}