package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val menuWidth = 100.dp

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

    // Manejar botón de retroceso
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
                    navController.popBackStack()
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
                    onClick = {
                        navController.navigate("listaEspacios")
                        isMenuVisible = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconWithText(
                    iconResId = R.drawable.geocercado,
                    label = "Geocercas",
                    onClick = {
                        navController.navigate("geofences")
                        isMenuVisible = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconWithText(
                    iconResId = R.drawable.advertencia,
                    label = "Alertas",
                    onClick = {
                        navController.navigate("alerts")
                        isMenuVisible = false
                    }
                )
            }
        }

        // Contenido Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Encabezado fijo con botón de menú y título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isMenuVisible = !isMenuVisible },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isMenuVisible) Icons.Default.ChevronLeft else Icons.Default.Menu,
                        contentDescription = "Toggle Menu",
                        tint = Color(0xFFE74C3C)
                    )
                }

                Text(
                    text = espacio?.nombre ?: "Corral de vaquitas",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            // Botones de acción fijos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón Agregar
                ActionButton(
                    iconResId = R.drawable.agregar,
                    label = "Agregar",
                    color = Color(0xFF0F9D58),
                    onClick = {
                        espacio?.let {
                            navController.navigate("agregarAnimal/${it.idEspacio}")
                        }
                    }
                )

                // Botón Editar
                ActionButton(
                    iconResId = R.drawable.modificar,
                    label = "Editar",
                    color = Color(0xFF4285F4),
                    onClick = {
                        espacio?.let {
                            navController.navigate("editarEspacio/${it.idEspacio}")
                        }
                    }
                )

                // Botón Activar/Desactivar
                if (espacio?.estatus == true) {
                    ActionButton(
                        iconResId = R.drawable.eliminar,
                        label = "Desactivar",
                        color = Color.Red,
                        onClick = {
                            espacio?.let {
                                espacioViewModel.cambiarEstatusEspacio(it.idEspacio, false)
                            }
                        }
                    )
                } else {
                    ActionButton(
                        iconResId = R.drawable.activar, // Asegúrate de tener este drawable
                        label = "Activar",
                        color = Color(0xFF0F9D58),
                        onClick = {
                            espacio?.let {
                                espacioViewModel.cambiarEstatusEspacio(it.idEspacio, true)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido scrollable
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Tarjetas de estadísticas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    // Primera fila de tarjetas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CardEstadistica(
                            titulo = "CANTIDAD",
                            valor = animalesEnEspacio.size,
                            icono = R.drawable.numeros,
                            colorIcono = Color(0xFF4285F4),
                            colorFondo = Color(0xFFE8F0FE)
                        )
                        CardEstadistica(
                            titulo = "ADULTOS",
                            valor = cantidadAdultos,
                            icono = R.drawable.vaca,
                            colorIcono = Color(0xFF0F9D58),
                            colorFondo = Color(0xFFE6F4EA)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Segunda fila de tarjetas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CardEstadistica(
                            titulo = "CRÍAS",
                            valor = cantidadCrias,
                            icono = R.drawable.cria,
                            colorIcono = Color(0xFFF4B400),
                            colorFondo = Color(0xFFFEF7E0)
                        )
                        CardEstadistica(
                            titulo = "CRÍTICOS",
                            valor = cantidadCriticos,
                            icono = R.drawable.advertencia,
                            colorIcono = Color(0xFFDB4437),
                            colorFondo = Color(0xFFFDECEA)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de animales
                Text(
                    text = "Animales | ${animalesEnEspacio.size}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(animalesEnEspacio) { animal ->
                        CardAnimal(
                            animal = animal,
                            razas = razas,
                            onVerDetalle = {
                                navController.navigate("editarAnimal/${animal.idAnimal}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
private fun ActionButton(
    iconResId: Int,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = color
            )
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black
        )
    }
}

@Composable
fun CardEstadistica(
    titulo: String,
    valor: Int,
    icono: Int,
    colorIcono: Color,
    colorFondo: Color
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(120.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$valor",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colorIcono
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardAnimal(
    animal: Animal,
    razas: List<Raza>,
    onVerDetalle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (animal.critico) Color(0x22FF0000) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.apodo,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sexo: ${animal.sexo}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Raza: ${razas.firstOrNull { it.idRaza == animal.razaId }?.nombre ?: "No especificada"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Edad: ${animal.obtenerEdad()} años",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = onVerDetalle,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFFE74C3C)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mostrar),
                    contentDescription = "Ver detalle",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
