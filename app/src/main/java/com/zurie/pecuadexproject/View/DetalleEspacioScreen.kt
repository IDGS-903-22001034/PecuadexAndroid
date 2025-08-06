package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Animal
import com.zurie.pecuadexproject.Data.Model.Raza
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.ViewModels.EspacioViewModel
import com.zurie.pecuadexproject.ViewModels.RazaViewModel
import com.zurie.pecuadexproject.components.SideBarLayout
import com.zurie.pecuadexproject.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetalleEspacioScreen(
    espacioId: Long,
    navController: NavHostController,
    espacioViewModel: EspacioViewModel = viewModel(),
    animalViewModel: AnimalViewModel = viewModel(),
    razaViewModel: RazaViewModel = viewModel()
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedAnimal by remember { mutableStateOf<Animal?>(null) }

    val espacioState = espacioViewModel.state
    val animalState = animalViewModel.state
    val razaState = razaViewModel.state

    val espacio = espacioState.espacioSeleccionado
    val animales = animalState.animales
    val razas = razaState.razas

    LaunchedEffect(espacioId) {
        espacioViewModel.obtenerEspacioId(espacioId)
        animalViewModel.obtenerAnimales()
        razaViewModel.obtenerRazas()
    }

    val animalesEnEspacio = animales.filter { it.espacioId?.toLong() == espacioId }
    val cantidadAdultos = animalesEnEspacio.count { !it.esCria() }
    val cantidadCrias = animalesEnEspacio.count { it.esCria() }
    val cantidadCriticos = animalesEnEspacio.count { it.critico }

    BackHandler(enabled = isMenuVisible) {
        isMenuVisible = false
    }

    SideBarLayout(
        isMenuVisible = isMenuVisible,
        onMenuToggle = { isMenuVisible = it },
        currentRoute = "detalleEspacio",
        onNavigate = { route -> navController.navigate(route) }
    ) {
        // Fondo con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.Background,
                            AppColors.Surface
                        )
                    )
                )
        ) {
            if (espacioState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.Primary)
                }
            } else if (espacio != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Header con información del espacio
                        EspacioHeader(
                            espacio = espacio,
                            onMenuClick = { isMenuVisible = !isMenuVisible },
                            isMenuVisible = isMenuVisible,
                            onEditClick = {
                                navController.navigate("editarEspacio/${espacio.idEspacio}")
                            },
                            onDeleteClick = { showDeleteDialog = true }
                        )
                    }

                    item {
                        // Estadísticas del espacio
                        EspacioStats(
                            totalAnimales = animalesEnEspacio.size,
                            adultos = cantidadAdultos,
                            crias = cantidadCrias,
                            criticos = cantidadCriticos
                        )
                    }

                    item {
                        // Acciones rápidas
                        QuickActions(
                            espacioId = espacioId,
                            navController = navController
                        )
                    }

                    item {
                        // Header de lista de animales
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Animales en el Espacio",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.OnBackground
                            )

                            Text(
                                "${animalesEnEspacio.size} animales",
                                fontSize = 14.sp,
                                color = AppColors.Muted
                            )
                        }
                    }

                    if (animalesEnEspacio.isEmpty()) {
                        item {
                            EmptyAnimalsState(
                                onAddAnimal = {
                                    navController.navigate("agregarAnimal/$espacioId")
                                }
                            )
                        }
                    } else {
                        items(animalesEnEspacio) { animal ->
                            ModernAnimalCard(
                                animal = animal,
                                razas = razas,
                                onClick = { selectedAnimal = animal },
                                onEditClick = {
                                    navController.navigate("editarAnimal/${animal.idAnimal}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar espacio
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar Espacio",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Error
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar este espacio? Esta acción no se puede deshacer y afectará a todos los animales asociados.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        espacioViewModel.eliminarEspacio(
                            espacioId,
                            onSuccess = {
                                navController.navigate("listaEspacios") {
                                    popUpTo("listaEspacios") { inclusive = true }
                                }
                            },
                            onError = { /* Manejar error */ }
                        )
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.OnSurface
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EspacioHeader(
    espacio: com.zurie.pecuadexproject.Data.Model.Espacio,
    onMenuClick: () -> Unit,
    isMenuVisible: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        // Fila superior con menú y acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de menú
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onMenuClick() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMenuVisible) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Botón editar
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onEditClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Info.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = AppColors.Info,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Botón eliminar
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onDeleteClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Error.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = AppColors.Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Información del espacio
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono del espacio
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.Primary.copy(alpha = 0.15f),
                                        AppColors.Secondary.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.establo),
                            contentDescription = "Espacio",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Información básica
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = espacio.nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = espacio.direccion,
                            fontSize = 14.sp,
                            color = AppColors.Muted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Estado y raza
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Estado
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (espacio.estatus)
                                            AppColors.Success.copy(alpha = 0.15f)
                                        else
                                            AppColors.Error.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (espacio.estatus) "Activo" else "Inactivo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (espacio.estatus) AppColors.Success else AppColors.Error
                                )
                            }

                            // Raza
                            espacio.raza?.let { raza ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppColors.Secondary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = raza.nombre,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.Secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EspacioStats(
    totalAnimales: Int,
    adultos: Int,
    crias: Int,
    criticos: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Estadísticas del Rebaño",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatCard(
                        title = "Total",
                        value = totalAnimales.toString(),
                        icon = Icons.Outlined.Pets,
                        color = AppColors.Primary
                    )
                }
                item {
                    StatCard(
                        title = "Adultos",
                        value = adultos.toString(),
                        icon = Icons.Outlined.LocalFlorist,
                        color = AppColors.Success
                    )
                }
                item {
                    StatCard(
                        title = "Crías",
                        value = crias.toString(),
                        icon = Icons.Outlined.ChildCare,
                        color = AppColors.Warning
                    )
                }
                item {
                    StatCard(
                        title = "Críticos",
                        value = criticos.toString(),
                        icon = Icons.Outlined.LocalHospital,
                        color = AppColors.Error
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActions(
    espacioId: Long,
    navController: NavHostController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Acciones Rápidas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    title = "Agregar Animal",
                    icon = Icons.Outlined.Add,
                    color = AppColors.Primary,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("agregarAnimal/$espacioId")
                }

                ActionButton(
                    title = "Ver en Mapa",
                    icon = Icons.Outlined.Map,
                    color = AppColors.Info,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("mapa")
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ModernAnimalCard(
    animal: Animal,
    razas: List<Raza>,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (animal.critico)
                AppColors.Error.copy(alpha = 0.05f)
            else
                AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (animal.critico)
                AppColors.Error.copy(alpha = 0.2f)
            else
                AppColors.Border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del animal
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (animal.critico)
                            AppColors.Error.copy(alpha = 0.15f)
                        else if (animal.esCria())
                            AppColors.Warning.copy(alpha = 0.15f)
                        else
                            AppColors.Success.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vaca),
                    contentDescription = "Animal",
                    tint = if (animal.critico)
                        AppColors.Error
                    else if (animal.esCria())
                        AppColors.Warning
                    else
                        AppColors.Success,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del animal
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = animal.apodo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )

                    if (animal.critico) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.Error)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "CRÍTICO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.OnError
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sexo: ${if (animal.sexo == "M") "Macho" else "Hembra"} • " +
                            "Edad: ${animal.obtenerEdad()} años • " +
                            "Peso: ${animal.peso} kg",
                    fontSize = 14.sp,
                    color = AppColors.Muted
                )

                razas.firstOrNull { it.idRaza == animal.razaId }?.let { raza ->
                    Text(
                        text = "Raza: ${raza.nombre}",
                        fontSize = 12.sp,
                        color = AppColors.Secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Botón de editar
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Editar animal",
                    tint = AppColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyAnimalsState(
    onAddAnimal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Pets,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Sin animales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Text(
                "Este espacio aún no tiene animales asignados",
                fontSize = 14.sp,
                color = AppColors.Muted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddAnimal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Animal")
            }
        }
    }
}