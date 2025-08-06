package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.MainViewModel
import com.zurie.pecuadexproject.components.StatusType
import com.zurie.pecuadexproject.ui.theme.AppColors
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var showInfoPanel by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val gpsData by viewModel.currentGpsData.collectAsState()
    val allAnimalsData by viewModel.allAnimalsData.collectAsState()
    val geofenceArea by viewModel.geofenceArea.collectAsState()
    val isInsideGeofence by viewModel.isInsideGeofence.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val isSimulationActive by viewModel.isSimulationActive.collectAsState()

    // COORDENADAS Y RADIO FIJOS PARA LA GEOCERCA
    // ⚠️ CAMBIAR ESTAS COORDENADAS POR LAS QUE NECESITES
    val GEOCERCA_LATITUD = 21.063562939245507    // Latitud fija
    val GEOCERCA_LONGITUD = -101.58053658565431  // Longitud fija
    val GEOCERCA_RADIO = 20.0f           // Radio en metros

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val defaultLocation = LatLng(GEOCERCA_LATITUD, GEOCERCA_LONGITUD)
    val currentLocation = gpsData?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 18f)
    }

    // Crear la geocerca automáticamente al inicio
    LaunchedEffect(Unit) {
        viewModel.setGeofenceCenter(GEOCERCA_LATITUD, GEOCERCA_LONGITUD, GEOCERCA_RADIO)

        // Iniciar simulación automáticamente
        viewModel.startSimulation()
    }

    // Centrar la cámara en la ubicación del animal cuando se reciba nueva data GPS
    LaunchedEffect(currentLocation) {
        if (gpsData != null) { // Solo animar si hay datos GPS reales
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(currentLocation, 18f)
                    ),
                    1000
                )
            } catch (e: Exception) {
                println("Camera animation cancelled: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Remover el onMapLongClick ya que la geocerca es fija
            onMapClick = { /* La geocerca es fija, no se puede cambiar */ }
        ) {
            // Mostrar todos los animales (real + simulados)
            allAnimalsData.forEach { animalData ->
                // ✅ Tu ESP32 se identifica como "ESP32_GPS_Client"
                val isRealAnimal = animalData.deviceId == "REAL_DEVICE" ||
                        animalData.deviceId.contains("ESP32") || // ← Detecta tu ESP32
                        animalData.deviceId == "ESP32_GPS_Client" // ← Específico para tu device

                Marker(
                    state = MarkerState(position = LatLng(animalData.latitude, animalData.longitude)),
                    title = if (isRealAnimal) "🔥 TU DISPOSITIVO REAL (${animalData.deviceId})" else getAnimalDisplayName(animalData.deviceId),
                    snippet = "Última actualización: ${animalData.time} | Velocidad: ${"%.1f".format(animalData.speed)} km/h",
                    icon = when {
                        isRealAnimal -> {
                            if (isInsideGeofence)
                                com.zurie.pecuadexproject.utils.CustomMarkerUtils.createRealAnimalMarker(context)
                            else
                                com.zurie.pecuadexproject.utils.CustomMarkerUtils.createDangerAnimalMarker(context)
                        }
                        animalData.deviceId.contains("BULL") ->
                            com.zurie.pecuadexproject.utils.CustomMarkerUtils.createBullMarker(context)
                        animalData.deviceId.contains("CALF") ->
                            com.zurie.pecuadexproject.utils.CustomMarkerUtils.createCalfMarker(context)
                        animalData.deviceId.contains("SHEEP") ->
                            com.zurie.pecuadexproject.utils.CustomMarkerUtils.createSheepMarker(context)
                        animalData.deviceId.contains("GOAT") ->
                            com.zurie.pecuadexproject.utils.CustomMarkerUtils.createGoatMarker(context)
                        else ->
                            com.zurie.pecuadexproject.utils.CustomMarkerUtils.createAnimalMarker(context)
                    }
                )
            }

            // Círculo de la geocerca (siempre visible con coordenadas fijas)
            Circle(
                center = LatLng(GEOCERCA_LATITUD, GEOCERCA_LONGITUD),
                radius = GEOCERCA_RADIO.toDouble(),
                fillColor = if (isInsideGeofence)
                    AppColors.GeofenceInside.copy(alpha = 0.2f)
                else
                    AppColors.GeofenceOutside.copy(alpha = 0.2f),
                strokeColor = if (isInsideGeofence)
                    AppColors.GeofenceInside
                else
                    AppColors.GeofenceOutside,
                strokeWidth = 3f
            )

            // Marcador del centro de la geocerca (opcional)
            Marker(
                state = MarkerState(position = LatLng(GEOCERCA_LATITUD, GEOCERCA_LONGITUD)),
                title = "Centro de Geocerca",
                snippet = "Zona Segura - Radio: ${GEOCERCA_RADIO}m",
                icon = com.zurie.pecuadexproject.utils.CustomMarkerUtils.createGeofenceCenterMarker(context)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .zIndex(3f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .clickable { isMenuVisible = !isMenuVisible },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMenuVisible) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { showInfoPanel = !showInfoPanel },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showInfoPanel)
                            AppColors.Primary.copy(alpha = 0.1f)
                        else
                            AppColors.Surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Toggle info",
                            tint = if (showInfoPanel) AppColors.Primary else AppColors.Muted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Botón para toggle simulación
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            viewModel.toggleSimulation()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSimulationActive)
                            AppColors.Success.copy(alpha = 0.1f)
                        else
                            AppColors.Surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSimulationActive) Icons.Outlined.Stop else Icons.Outlined.PlayArrow,
                            contentDescription = if (isSimulationActive) "Detener simulación" else "Iniciar simulación",
                            tint = if (isSimulationActive) AppColors.Success else AppColors.Muted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                gpsData?.let { data ->
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                scope.launch {
                                    try {
                                        val animalPosition = LatLng(data.latitude, data.longitude)
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newCameraPosition(
                                                CameraPosition.fromLatLngZoom(animalPosition, 18f)
                                            ),
                                            1000
                                        )
                                    } catch (e: Exception) {
                                        println("Error animating to animal position: ${e.message}")
                                    }
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MyLocation,
                                contentDescription = "Centrar en animal",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Botón para centrar en la geocerca
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            scope.launch {
                                try {
                                    val bounds = com.google.android.gms.maps.model.LatLngBounds.builder()
                                        .include(LatLng(GEOCERCA_LATITUD + 0.001, GEOCERCA_LONGITUD + 0.001))
                                        .include(LatLng(GEOCERCA_LATITUD - 0.001, GEOCERCA_LONGITUD - 0.001))
                                        .build()

                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngBounds(bounds, 100),
                                        1000
                                    )
                                } catch (e: Exception) {
                                    println("Error animating to bounds: ${e.message}")
                                }
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ZoomOutMap,
                            contentDescription = "Ver geocerca",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showInfoPanel,
            enter = slideInHorizontally(
                initialOffsetX = { 0 },
                animationSpec = tween(durationMillis = 300)
            ) + androidx.compose.animation.fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { 0 },
                animationSpec = tween(durationMillis = 300)
            ) + androidx.compose.animation.fadeOut(
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .zIndex(2f)
        ) {
            CompactInfoPanel(
                connectionStatus = connectionStatus,
                gpsData = gpsData,
                geofenceArea = geofenceArea,
                isInsideGeofence = isInsideGeofence
            )
        }

        // Información del rebaño (reemplaza el mensaje de instrucciones)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rebaño Monitoreado",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )
                        Text(
                            text = if (isSimulationActive)
                                "${allAnimalsData.size} animales activos"
                            else "Solo animal real",
                            fontSize = 12.sp,
                            color = AppColors.Muted
                        )
                    }

                    // Indicador de simulación
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSimulationActive)
                                    AppColors.Success.copy(alpha = 0.1f)
                                else
                                    AppColors.Muted.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSimulationActive) "SIM ON" else "SIM OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSimulationActive) AppColors.Success else AppColors.Muted
                        )
                    }
                }

                if (isSimulationActive && allAnimalsData.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    val flockStats = viewModel.getFlockStats()

                    // Estadísticas del rebaño
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimalTypeChip("🐄", flockStats.cows, "Vacas")
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Geocerca: Radio ${GEOCERCA_RADIO}m • Lat: ${String.format("%.6f", GEOCERCA_LATITUD)} • Lng: ${String.format("%.6f", GEOCERCA_LONGITUD)}",
                        fontSize = 11.sp,
                        color = AppColors.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isMenuVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)),
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .zIndex(4f)
        ) {
            ModernSideMenu(
                currentRoute = "mapa",
                onNavigate = { route ->
                    navController.navigate(route)
                    isMenuVisible = false
                },
                onClose = { isMenuVisible = false }
            )
        }
    }

    if (!locationPermissions.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
}

@Composable
private fun CompactInfoPanel(
    connectionStatus: Boolean,
    gpsData: com.zurie.pecuadexproject.Data.Model.GpsData?,
    geofenceArea: com.zurie.pecuadexproject.Data.Model.GeofenceArea?,
    isInsideGeofence: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp) // Espacio para no chocar con los botones
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header compacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Estado del Sistema",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (connectionStatus) AppColors.Success else AppColors.Error)
                )
            }

            // Fila con los estados principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado de conexión
                CompactStatusItem(
                    icon = Icons.Outlined.Wifi,
                    value = if (connectionStatus) "ON" else "OFF",
                    color = if (connectionStatus) AppColors.Success else AppColors.Error
                )

                // GPS y satélites
                gpsData?.let { data ->
                    CompactStatusItem(
                        icon = Icons.Outlined.Satellite,
                        value = "${data.satellites}",
                        color = when {
                            data.satellites >= 6 -> AppColors.Success
                            data.satellites >= 4 -> AppColors.Warning
                            else -> AppColors.Error
                        }
                    )
                } ?: CompactStatusItem(
                    icon = Icons.Outlined.Satellite,
                    value = "0",
                    color = AppColors.Error
                )

                // Estado de geocerca
                geofenceArea?.let { area ->
                    CompactStatusItem(
                        icon = if (isInsideGeofence) Icons.Outlined.Shield else Icons.Outlined.Warning,
                        value = if (isInsideGeofence) "OK" else "OUT",
                        color = if (isInsideGeofence) AppColors.Success else AppColors.Error
                    )
                } ?: CompactStatusItem(
                    icon = Icons.Outlined.Shield,
                    value = "N/A",
                    color = AppColors.Muted
                )

                // Velocidad (si hay datos GPS)
                gpsData?.let { data ->
                    CompactStatusItem(
                        icon = Icons.Outlined.Speed,
                        value = "${data.speed.toInt()}",
                        color = AppColors.Primary
                    )
                }
            }

            // Información adicional compacta
            gpsData?.let { data ->
                val distance = geofenceArea?.let { area ->
                    com.zurie.pecuadexproject.utils.LocationUtils.calculateDistance(
                        data.latitude, data.longitude,
                        area.centerLatitude, area.centerLongitude
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = distance?.let { "Dist: ${"%.1f".format(it)}m" } ?: "Sin geocerca",
                        fontSize = 11.sp,
                        color = AppColors.Muted
                    )
                    Text(
                        text = " • ${data.time}",
                        fontSize = 11.sp,
                        color = AppColors.Muted
                    )
                }
            } ?: Text(
                text = "Esperando datos GPS...",
                fontSize = 11.sp,
                color = AppColors.Muted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompactStatusItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ModernSideMenu(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxHeight(),
        color = AppColors.Primary,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppColors.OnPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.vaca),
                            contentDescription = "Logo",
                            tint = AppColors.OnPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "PecuaDex",
                        color = AppColors.OnPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = AppColors.OnPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MenuItemCard(
                icon = Icons.Outlined.Dashboard,
                title = "Tablero",
                subtitle = "Vista general",
                isSelected = currentRoute == "principal",
                onClick = { onNavigate("principal") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "MÓDULOS",
                color = AppColors.OnPrimary.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuItemCard(
                icon = Icons.Outlined.Home,
                title = "Espacios",
                subtitle = "Gestionar corrales",
                onClick = { onNavigate("listaEspacios") }
            )

            MenuItemCard(
                icon = Icons.Outlined.LocationOn,
                title = "Geocercas",
                subtitle = "Control de ubicación",
                isSelected = currentRoute == "mapa",
                onClick = { /* Current screen */ }
            )

            MenuItemCard(
                icon = Icons.Outlined.Warning,
                title = "Alertas",
                subtitle = "Notificaciones",
                onClick = { onNavigate("alertas") }
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                AppColors.OnPrimary.copy(alpha = 0.15f)
            else
                Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AppColors.OnPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = AppColors.OnPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = AppColors.OnPrimary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AnimalTypeChip(emoji: String, count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 16.sp
        )
        Text(
            text = count.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = AppColors.Muted
        )
    }
}

// Función auxiliar para obtener nombre del animal
private fun getAnimalDisplayName(deviceId: String): String {
    return when {
        deviceId.contains("COW") -> {
            val number = deviceId.substringAfterLast("_")
            "Vaca $number"
        }
        deviceId.contains("BULL") -> {
            val number = deviceId.substringAfterLast("_")
            "Toro $number"
        }
        deviceId.contains("CALF") -> {
            val number = deviceId.substringAfterLast("_")
            "Cría $number"
        }
        deviceId.contains("SHEEP") -> {
            val number = deviceId.substringAfterLast("_")
            "Oveja $number"
        }
        deviceId.contains("GOAT") -> {
            val number = deviceId.substringAfterLast("_")
            "Cabra $number"
        }
        else -> deviceId
    }
}