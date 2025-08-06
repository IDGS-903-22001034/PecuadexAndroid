package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
    val geofenceArea by viewModel.geofenceArea.collectAsState()
    val isInsideGeofence by viewModel.isInsideGeofence.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val defaultLocation = LatLng(20.9407255, -101.4392308)
    val currentLocation = gpsData?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 18f)
    }

    LaunchedEffect(currentLocation) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                viewModel.setGeofenceCenter(latLng.latitude, latLng.longitude)
            }
        ) {
            gpsData?.let { data ->
                Marker(
                    state = MarkerState(position = LatLng(data.latitude, data.longitude)),
                    title = "Animal Rastreado",
                    snippet = "Última actualización: ${data.time}",
                    icon = if (isInsideGeofence)
                        com.zurie.pecuadexproject.utils.CustomMarkerUtils.createAnimalMarker(context)
                    else
                        com.zurie.pecuadexproject.utils.CustomMarkerUtils.createDangerAnimalMarker(context)
                )
            }

            geofenceArea?.let { area ->
                Circle(
                    center = LatLng(area.centerLatitude, area.centerLongitude),
                    radius = area.radiusMeters.toDouble(),
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
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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

                geofenceArea?.let { area ->
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                scope.launch {
                                    try {
                                        val bounds = com.google.android.gms.maps.model.LatLngBounds.builder()
                                            .include(LatLng(area.centerLatitude + 0.001, area.centerLongitude + 0.001))
                                            .include(LatLng(area.centerLatitude - 0.001, area.centerLongitude - 0.001))
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
                                contentDescription = "Ver toda la geocerca",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showInfoPanel,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 90.dp, end = 16.dp)
                .width(320.dp)
                .zIndex(2f)
        ) {
            ImprovedInfoPanel(
                connectionStatus = connectionStatus,
                gpsData = gpsData,
                geofenceArea = geofenceArea,
                isInsideGeofence = isInsideGeofence
            )
        }

        if (geofenceArea == null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.Info.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TouchApp,
                            contentDescription = "Instrucción",
                            tint = AppColors.Info,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crear Zona Segura",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )
                        Text(
                            text = "Mantén presionado en el mapa para establecer una geocerca",
                            fontSize = 14.sp,
                            color = AppColors.Muted,
                            lineHeight = 18.sp
                        )
                    }
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
private fun ImprovedInfoPanel(
    connectionStatus: Boolean,
    gpsData: com.zurie.pecuadexproject.Data.Model.GpsData?,
    geofenceArea: com.zurie.pecuadexproject.Data.Model.GeofenceArea?,
    isInsideGeofence: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier.border(1.dp, AppColors.Border, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Estado del Sistema",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (connectionStatus) AppColors.Success else AppColors.Error)
                )
            }

            InfoCard(
                icon = Icons.Outlined.Wifi,
                title = "Conexión MQTT",
                value = if (connectionStatus) "Conectado" else "Desconectado",
                status = if (connectionStatus) StatusType.SUCCESS else StatusType.ERROR
            )

            gpsData?.let { data ->
                InfoCard(
                    icon = Icons.Outlined.Satellite,
                    title = "Señal GPS",
                    value = "${data.satellites} satélites",
                    subtitle = "Precisión: ${"%.1f".format(data.hdop)}",
                    status = when {
                        data.satellites >= 6 -> StatusType.SUCCESS
                        data.satellites >= 4 -> StatusType.WARNING
                        else -> StatusType.ERROR
                    }
                )

                InfoCard(
                    icon = Icons.Outlined.Speed,
                    title = "Velocidad",
                    value = "${"%.1f".format(data.speed)} km/h",
                    subtitle = "Altitud: ${"%.0f".format(data.altitude)}m",
                    status = StatusType.NEUTRAL
                )
            }

            geofenceArea?.let { area ->
                val distance = gpsData?.let { data ->
                    com.zurie.pecuadexproject.utils.LocationUtils.calculateDistance(
                        data.latitude, data.longitude,
                        area.centerLatitude, area.centerLongitude
                    )
                }

                InfoCard(
                    icon = if (isInsideGeofence) Icons.Outlined.Shield else Icons.Outlined.Warning,
                    title = "Zona Segura",
                    value = if (isInsideGeofence) "Animal Seguro" else "Fuera de Zona",
                    subtitle = distance?.let { "Distancia: ${"%.1f".format(it)}m" },
                    status = if (isInsideGeofence) StatusType.SUCCESS else StatusType.ERROR
                )
            }

            gpsData?.let { data ->
                Text(
                    "Última actualización: ${data.date} ${data.time}",
                    fontSize = 12.sp,
                    color = AppColors.Muted,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String? = null,
    status: StatusType
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                StatusType.SUCCESS -> AppColors.Success.copy(alpha = 0.05f)
                StatusType.ERROR -> AppColors.Error.copy(alpha = 0.05f)
                StatusType.WARNING -> AppColors.Warning.copy(alpha = 0.05f)
                StatusType.NEUTRAL -> AppColors.Gray100
            }
        ),
        border = BorderStroke(
            1.dp,
            when (status) {
                StatusType.SUCCESS -> AppColors.Success.copy(alpha = 0.2f)
                StatusType.ERROR -> AppColors.Error.copy(alpha = 0.2f)
                StatusType.WARNING -> AppColors.Warning.copy(alpha = 0.2f)
                StatusType.NEUTRAL -> AppColors.Border
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (status) {
                            StatusType.SUCCESS -> AppColors.Success.copy(alpha = 0.15f)
                            StatusType.ERROR -> AppColors.Error.copy(alpha = 0.15f)
                            StatusType.WARNING -> AppColors.Warning.copy(alpha = 0.15f)
                            StatusType.NEUTRAL -> AppColors.Primary.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = when (status) {
                        StatusType.SUCCESS -> AppColors.Success
                        StatusType.ERROR -> AppColors.Error
                        StatusType.WARNING -> AppColors.Warning
                        StatusType.NEUTRAL -> AppColors.Primary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = AppColors.Muted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = AppColors.Muted
                    )
                }
            }
        }
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
