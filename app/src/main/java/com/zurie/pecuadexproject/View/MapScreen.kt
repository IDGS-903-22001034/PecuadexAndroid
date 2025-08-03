package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Pets
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
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.MainViewModel
import com.zurie.pecuadexproject.components.StatusCard
import com.zurie.pecuadexproject.components.StatusType
import com.amiigood.geofenceanimaltracker.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    val menuWidth = 100.dp

    val context = LocalContext.current
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
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(currentLocation, 18f)
            ),
            1000
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Menú Lateral
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 300)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 300)),
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

        // Contenido del Mapa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            // Botón para mostrar/ocultar el menú
            IconButton(
                onClick = { isMenuVisible = !isMenuVisible },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isMenuVisible) Icons.Default.ChevronLeft else Icons.Default.Menu,
                    contentDescription = "Toggle Menu",
                    tint = Color(0xFFE74C3C)
                )
            }

            // Mapa
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
                        title = "Animal",
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
                        fillColor = if (isInsideGeofence) AppColors.Success.copy(alpha = 0.15f) else AppColors.Error.copy(alpha = 0.15f),
                        strokeColor = if (isInsideGeofence) AppColors.Success else AppColors.Error,
                        strokeWidth = 2f
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 16.dp, end = 16.dp)
            ) {
                StatusCard(
                    title = "Conexión",
                    value = if (connectionStatus) "Conectado" else "Desconectado",
                    status = if (connectionStatus) StatusType.SUCCESS else StatusType.ERROR
                )

                Spacer(modifier = Modifier.height(8.dp))

                gpsData?.let { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = "Animal",
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dispositivo rastreador",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.OnSurface
                                )
                                Text(
                                    text = "${data.satellites} satélites conectados",
                                    fontSize = 12.sp,
                                    color = AppColors.Muted
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format("%.1f", data.hdop),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.OnSurface
                                )
                                Text(
                                    text = "precisión",
                                    fontSize = 12.sp,
                                    color = AppColors.Muted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                geofenceArea?.let { area ->
                    val distance = gpsData?.let { data ->
                        com.zurie.pecuadexproject.utils.LocationUtils.calculateDistance(
                            data.latitude, data.longitude,
                            area.centerLatitude, area.centerLongitude
                        )
                    }

                    StatusCard(
                        title = "Zona segura",
                        value = if (isInsideGeofence) "Animal seguro" else "Fuera de zona",
                        subtitle = distance?.let { "Distancia: ${String.format("%.1f", it)}m" },
                        status = if (isInsideGeofence) StatusType.SUCCESS else StatusType.ERROR
                    )
                }
            }

            if (geofenceArea == null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppColors.Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "i",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Mantén presionado en el mapa para crear una zona segura",
                            fontSize = 14.sp,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (!locationPermissions.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            locationPermissions.launchMultiplePermissionRequest()
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