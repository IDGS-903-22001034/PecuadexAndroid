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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.MainViewModel
import com.amiigood.geofenceanimaltracker.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    val menuWidth = 100.dp
    val alertHistory by viewModel.alertHistory.collectAsState()

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

        // Contenido Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(16.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Alertas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnBackground
                    )
                    Text(
                        text = "${alertHistory.size} alertas registradas",
                        fontSize = 14.sp,
                        color = AppColors.Muted
                    )
                }

                if (alertHistory.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearAlertHistory() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Limpiar historial",
                            tint = AppColors.Secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (alertHistory.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(AppColors.Success.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = AppColors.Success,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Sin alertas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.OnSurface
                        )

                        Text(
                            text = "Las alertas aparecerán aquí cuando el dispositivo salga de la zona segura",
                            fontSize = 14.sp,
                            color = AppColors.Muted,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(alertHistory.reversed()) { alert ->
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
                                        .background(AppColors.Error.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = AppColors.Error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Zona segura abandonada",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.OnSurface
                                    )

                                    Text(
                                        text = alert.substringAfter(" - "),
                                        fontSize = 12.sp,
                                        color = AppColors.Muted
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