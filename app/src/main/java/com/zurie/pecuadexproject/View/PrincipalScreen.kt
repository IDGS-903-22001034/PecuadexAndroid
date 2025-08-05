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
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrincipalScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) } // Menú visible por defecto
    val menuWidth = 100.dp // Mismo tamaño que la versión anterior

    // ViewModel para datos de animales
    val animalViewModel: AnimalViewModel = viewModel()
    val animalState = animalViewModel.state

    // Calcular estadísticas
    val animales = animalState.animales
    val cantidadTotal = animales.size
    val cantidadAdultos = animales.count { !it.esCria() }
    val cantidadCrias = animales.count { it.esCria() }
    val cantidadCriticos = animales.count { it.critico }

    // Obtener datos al cargar
    LaunchedEffect(Unit) {
        animalViewModel.obtenerAnimales()
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            // Botón para mostrar/ocultar el menú
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

            Spacer(modifier = Modifier.height(8.dp))

            // Título del tablero
            Text(
                "TABLERO",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de estadísticas con datos reales
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                // Primera fila de tarjetas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardEstadistica(
                        titulo = "TOTAL ANIMALES",
                        valor = cantidadTotal,
                        icono = R.drawable.numeros,
                        colorIcono = Color(0xFF4285F4),
                        colorFondo = Color(0xFFE8F0FE),
                        onClick = { navController.navigate("animals") }
                    )

                    CardEstadistica(
                        titulo = "ADULTOS",
                        valor = cantidadAdultos,
                        icono = R.drawable.vaca,
                        colorIcono = Color(0xFF0F9D58),
                        colorFondo = Color(0xFFE6F4EA),
                        onClick = { navController.navigate("animals") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Segunda fila de tarjetas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardEstadistica(
                        titulo = "CRÍAS",
                        valor = cantidadCrias,
                        icono = R.drawable.cria,
                        colorIcono = Color(0xFFF4B400),
                        colorFondo = Color(0xFFFEF7E0),
                        onClick = { navController.navigate("offspring") }
                    )

                    CardEstadistica(
                        titulo = "CRÍTICOS",
                        valor = cantidadCriticos,
                        icono = R.drawable.advertencia,
                        colorIcono = Color(0xFFDB4437),
                        colorFondo = Color(0xFFFDECEA),
                        onClick = { navController.navigate("critical") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de alertas
            AlertSection(navController)
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
fun CardEstadistica(
    titulo: String,
    valor: Int,
    icono: Int,
    colorIcono: Color,
    colorFondo: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp) // Cards más pequeñas
            .height(120.dp), // Cards más pequeñas
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp), // Padding reducido
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(24.dp) // Icono más pequeño
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio reducido
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$valor",
                style = MaterialTheme.typography.titleLarge.copy( // Tamaño de texto reducido
                    fontWeight = FontWeight.Bold
                ),
                color = colorIcono
            )
        }
    }
}

@Composable
private fun AlertSection(navController: NavHostController) {
    var showAlerts by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ALERTAS RECIENTES",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE74C3C))

                IconButton(
                    onClick = { showAlerts = !showAlerts },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (showAlerts) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Alerts"
                    )
                }
            }

            if (showAlerts) {
                Spacer(modifier = Modifier.height(8.dp))

                // Alerta 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("alertDetail/1") }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.advertencia),
                        contentDescription = "Alerta",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Animal en zona restringida",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Vaca #123 en zona de cuarentena",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "15 min",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Alerta 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("alertDetail/2") }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.advertencia),
                        contentDescription = "Alerta",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Temperatura crítica detectada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Toro #456 con fiebre",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "2 horas",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}