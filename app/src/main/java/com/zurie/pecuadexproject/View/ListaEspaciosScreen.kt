package com.zurie.pecuadexproject.View

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.ViewModels.EspacioViewModel

@Composable
fun ListaEspaciosScreen(
    navController: NavHostController,
    espacioViewModel: EspacioViewModel = viewModel(),
    animalViewModel: AnimalViewModel = viewModel(),
) {
    var isMenuVisible by remember { mutableStateOf(true) }
    val menuWidth = 100.dp

    val estado = espacioViewModel.state
    val animalState = animalViewModel.state
    var filtroActivo by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        espacioViewModel.obtenerEspacios()
        animalViewModel.obtenerAnimales()
    }

    BackHandler(enabled = isMenuVisible) {
        isMenuVisible = false
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Menú Lateral
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.width(menuWidth)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(0xFFE74C3C)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.vaca),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                IconWithText(iconResId = R.drawable.casa, label = "Tablero") {
                    navController.navigate("principal")
                    isMenuVisible = false
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (isMenuVisible) {
                    Text("MÓDULOS", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                IconWithText(
                    iconResId = R.drawable.establo,
                    label = "Espacios",
                    onClick = { isMenuVisible = false }
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconWithText(
                    iconResId = R.drawable.geocercado,
                    label = "Geocercas",
                    onClick = {
                        navController.navigate("mapa")
                        isMenuVisible = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconWithText(
                    iconResId = R.drawable.advertencia,
                    label = "Alertas",
                    onClick = {
                        navController.navigate("alertas")
                        isMenuVisible = false
                    }
                )
            }
        }

        // Contenido Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { isMenuVisible = !isMenuVisible },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = if (isMenuVisible) Icons.Default.ChevronLeft else Icons.Default.Menu,
                    contentDescription = "Toggle Menu",
                    tint = Color(0xFFE74C3C)
                )
            }

            estado.error?.let { error ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = Color.Red)
                }
                return@Column
            }

            Text(
                text = "Mis espacios",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = filtroActivo,
                    onClick = { filtroActivo = true },
                    label = { Text("Activos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = !filtroActivo,
                    onClick = { filtroActivo = false },
                    label = { Text("Inactivos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF44336),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (estado.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(estado.espacios.filter { it.estatus == filtroActivo }) { espacio ->
                        val cantidadAnimales = animalState.animales.count {
                            it.espacioId?.toLong() == espacio.idEspacio
                        }

                        EspacioCard(
                            espacio = espacio,
                            cantidadAnimales = cantidadAnimales,
                            onClick = {
                                navController.navigate("detalleEspacio/${espacio.idEspacio}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate("espacios") },
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .heightIn(min = 36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4285F4),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                Text("Agregar espacio")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconWithText(
    iconResId: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun EspacioCard(
    espacio: Espacio,
    cantidadAnimales: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = espacio.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    espacio.raza?.let { raza ->
                        Text(
                            text = "Raza: ${raza.nombre}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } ?: Text(
                        text = "Sin raza asignada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Text(
                        text = "Dirección: ${espacio.direccion}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (cantidadAnimales > 0) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = "$cantidadAnimales",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (cantidadAnimales > 0) Color(0xFF0F9D58) else Color.Gray
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (espacio.estatus) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (espacio.estatus) Color(0xFF0F9D58) else Color(0xFFF44336)
                    )
                )

                Icon(
                    painter = painterResource(id = R.drawable.vaca),
                    contentDescription = "Icono espacio",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}