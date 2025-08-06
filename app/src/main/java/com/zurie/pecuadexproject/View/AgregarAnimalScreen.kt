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
import com.zurie.pecuadexproject.ui.theme.AppColors
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarAnimalScreen(
    espacioId: Long,
    navController: NavHostController,
    animalViewModel: AnimalViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel()
) {
    var apodo by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var razaSeleccionada by remember { mutableStateOf<Raza?>(null) }
    var peso by remember { mutableStateOf<Int?>(null) }
    var pesoText by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

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
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
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
                            tint = AppColors.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface,
                    titleContentColor = AppColors.OnSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Apodo del animal",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Muted,
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
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Border,
                    cursorColor = AppColors.Primary,
                    focusedLabelColor = AppColors.Primary
                ),
                placeholder = { Text("Ej: Lola, Torito") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Sexo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Muted,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                                selectedColor = AppColors.Primary
                            )
                        )
                        Text(
                            etiqueta,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(color = AppColors.OnSurface)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Raza",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Muted,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                onClick = { showRazaDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AppColors.Surface,
                    contentColor = AppColors.OnSurface
                ),
                border = BorderStroke(1.dp, AppColors.Border)
            ) {
                Text(
                    razaSeleccionada?.nombre ?: "Selecciona una raza",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Peso (kg)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Muted,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = pesoText,
                onValueChange = {
                    pesoText = it
                    peso = it.toIntOrNull()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Border,
                    cursorColor = AppColors.Primary,
                    focusedLabelColor = AppColors.Primary
                ),
                placeholder = { Text("Ej: 450") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Fecha de nacimiento",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Muted,
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
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Border,
                    cursorColor = AppColors.Primary,
                    focusedLabelColor = AppColors.Primary
                ),
                placeholder = { Text("dd/mm/aaaa") },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = AppColors.Primary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppColors.Surface,
                        contentColor = AppColors.Muted
                    ),
                    border = BorderStroke(1.dp, AppColors.Border)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
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
                                        peso = peso!!,
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
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.OnPrimary
                    ),
                    enabled = !animalViewModel.state.isLoading
                ) {
                    if (animalViewModel.state.isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.OnPrimary,
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

    if (showRazaDialog) {
        AlertDialog(
            onDismissRequest = { showRazaDialog = false },
            title = {
                Text(
                    "Seleccionar raza",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
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
                            modifier = Modifier.padding(16.dp),
                            color = AppColors.Muted
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
                                        AppColors.GradientStart else AppColors.Surface
                                )
                            ) {
                                Text(
                                    text = raza.nombre,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = AppColors.OnSurface
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
                        contentColor = AppColors.Primary
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
                navController.popBackStack()
            },
            title = {
                Text(
                    "¡Éxito!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success
                    )
                )
            },
            text = {
                Text(
                    "El animal se ha agregado correctamente",
                    color = AppColors.OnSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Success,
                        contentColor = AppColors.OnPrimary
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
                        color = AppColors.Error,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    errorMessage,
                    color = AppColors.OnSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.Primary
                    )
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}
