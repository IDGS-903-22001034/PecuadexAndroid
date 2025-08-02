package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.ViewModels.RazaViewModel
import com.zurie.pecuadexproject.ViewModels.EnfermedadViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarAnimalScreen(
    animalId: Long,
    navController: NavHostController,
    animalViewModel: AnimalViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel(),
    enfermedadViewModel: EnfermedadViewModel = viewModel()
) {
    // Estados para los campos del formulario
    var apodo by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var razaSeleccionada by remember { mutableStateOf<Raza?>(null) }
    var peso by remember { mutableStateOf<Int?>(null) }
    var pesoText by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var critico by remember { mutableStateOf(false) }
    var enfermedadesSeleccionadas by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // Estados para los diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showRazaDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Estado para controlar la actualización de enfermedades
    var actualizarEnfermedades by remember { mutableStateOf(false) }

    val razas = razaViewModel.state.razas
    val enfermedades = enfermedadViewModel.state.enfermedades

    // Efecto para actualizar enfermedades después de modificar el animal
    LaunchedEffect(actualizarEnfermedades) {
        if (actualizarEnfermedades) {
            try {
                animalViewModel.actualizarEnfermedadesAnimal(
                    animalId,
                    enfermedadesSeleccionadas.toList(),
                    onSuccess = {
                        showSuccessDialog = true
                        actualizarEnfermedades = false
                    },
                    onError = { error ->
                        errorMessage = "Error al actualizar enfermedades: $error"
                        showErrorDialog = true
                        actualizarEnfermedades = false
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Error al actualizar enfermedades: ${e.message}"
                showErrorDialog = true
                actualizarEnfermedades = false
            }
        }
    }

    // Cargar datos del animal al iniciar
    LaunchedEffect(animalId) {
        try {
            // Cargar datos en paralelo
            coroutineScope {
                launch { animalViewModel.obtenerAnimalPorId(animalId) }
                launch { razaViewModel.obtenerRazas() }
                launch { enfermedadViewModel.obtenerEnfermedades() }
            }

            // Configurar los campos con los datos del animal
            animalViewModel.animalParaEditar?.let { animal ->
                apodo = animal.apodo
                sexo = animal.sexo
                peso = animal.peso
                pesoText = animal.peso.toString()
                fechaNacimiento = animal.fechaNacimiento
                critico = animal.critico

                // Buscar la raza correspondiente
                razaSeleccionada = razas.firstOrNull { it.idRaza == animal.razaId }

                // Cargar enfermedades del animal
                val enfermedadesIds = animalViewModel.obtenerEnfermedadesDelAnimal(animalId)
                enfermedadesSeleccionadas = enfermedadesIds.toSet()
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar datos: ${e.message}"
            showErrorDialog = true
        } finally {
            isLoading = false
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
                        "Editar Animal",
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
                actions = {
                    // Botón para marcar como crítico
                    IconButton(
                        onClick = { critico = !critico },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (critico) {
                            Icon(
                                painter = painterResource(id = R.drawable.critico),
                                contentDescription = "Crítico",
                                tint = Color(0xFFFFA000), // Amarillo
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.estable),
                                contentDescription = "Estable",
                                tint = Color(0xFF4CAF50), // Verde
                                modifier = Modifier.size(32.dp)
                            )
                        }
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

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de enfermedades con checkboxes
            Text(
                "Enfermedades",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(enfermedades) { enfermedad ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                enfermedadesSeleccionadas = if (enfermedadesSeleccionadas.contains(enfermedad.idEnfermedad)) {
                                    enfermedadesSeleccionadas - enfermedad.idEnfermedad
                                } else {
                                    enfermedadesSeleccionadas + enfermedad.idEnfermedad
                                }
                            }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Checkbox(
                            checked = enfermedadesSeleccionadas.contains(enfermedad.idEnfermedad),
                            onCheckedChange = { isChecked ->
                                enfermedadesSeleccionadas = if (isChecked) {
                                    enfermedadesSeleccionadas + enfermedad.idEnfermedad
                                } else {
                                    enfermedadesSeleccionadas - enfermedad.idEnfermedad
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4285F4)
                            )
                        )
                        Text(
                            text = enfermedad.nombre,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
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
                                    val animalActualizado = Animal(
                                        idAnimal = animalId,
                                        apodo = apodo,
                                        sexo = sexo,
                                        razaId = razaSeleccionada!!.idRaza,
                                        espacioId = animalViewModel.animalParaEditar?.espacioId,
                                        peso = peso!!,
                                        fechaNacimiento = fechaNacimiento,
                                        critico = critico,
                                        fechaFallecimiento = animalViewModel.animalParaEditar?.fechaFallecimiento
                                    )

                                    // Primero actualizar el animal
                                    animalViewModel.modificarAnimal(
                                        animalId,
                                        animalActualizado,
                                        onSuccess = {
                                            // Activamos la actualización de enfermedades
                                            actualizarEnfermedades = true
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                            showErrorDialog = true
                                        }
                                    )
                                } catch (e: Exception) {
                                    errorMessage = "Error al actualizar el animal: ${e.message}"
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
                Text("El animal se ha actualizado correctamente")
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
                Text("¿Estás seguro de que deseas eliminar este animal? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        animalViewModel.eliminarAnimal(
                            animalId,
                            onSuccess = {
                                navController.popBackStack()
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