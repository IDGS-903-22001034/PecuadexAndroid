package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.ViewModels.MainViewModel
import com.zurie.pecuadexproject.components.SideBarLayout
import com.zurie.pecuadexproject.ui.theme.AppColors


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlertsScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(AlertFilter.ALL) }

    val alertHistory by viewModel.alertHistory.collectAsState()

    BackHandler(enabled = isMenuVisible) {
        isMenuVisible = false
    }

    // Filtrar alertas según el filtro seleccionado
    val filteredAlerts = remember(alertHistory, selectedFilter) {
        when (selectedFilter) {
            AlertFilter.ALL -> alertHistory
            AlertFilter.TODAY -> alertHistory.filter {
                // Aquí podrías filtrar por fecha de hoy
                true // Por simplicidad, mostramos todas por ahora
            }
            AlertFilter.CRITICAL -> alertHistory.filter {
                it.contains("salió", ignoreCase = true) || it.contains("crítico", ignoreCase = true)
            }
        }
    }

    SideBarLayout(
        isMenuVisible = isMenuVisible,
        onMenuToggle = { isMenuVisible = it },
        currentRoute = "alertas",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header mejorado
                ImprovedAlertsHeader(
                    onMenuClick = { isMenuVisible = !isMenuVisible },
                    isMenuVisible = isMenuVisible,
                    totalAlerts = alertHistory.size,
                    onClearAll = { viewModel.clearAlertHistory() },
                    hasAlerts = alertHistory.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Filtros simplificados usando botones
                SimpleFilterButtons(
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    alertCounts = mapOf(
                        AlertFilter.ALL to alertHistory.size,
                        AlertFilter.TODAY to alertHistory.size, // Simplificado
                        AlertFilter.CRITICAL to alertHistory.count {
                            it.contains("salió", ignoreCase = true)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Contenido principal
                if (filteredAlerts.isEmpty()) {
                    EmptyAlertsState(
                        filter = selectedFilter,
                        onTestAlert = { viewModel.testAlert() }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredAlerts.reversed()) { alert ->
                            ModernAlertCard(
                                alert = alert,
                                onClick = { /* Mostrar detalles si es necesario */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovedAlertsHeader(
    onMenuClick: () -> Unit,
    isMenuVisible: Boolean,
    totalAlerts: Int,
    onClearAll: () -> Unit,
    hasAlerts: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Alertas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnBackground
                )
                Text(
                    "$totalAlerts alertas registradas",
                    fontSize = 14.sp,
                    color = AppColors.Muted
                )
            }
        }

        // Botón para limpiar alertas
        if (hasAlerts) {
            OutlinedButton(
                onClick = onClearAll,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.Error
                ),
                border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Limpiar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Limpiar", fontSize = 14.sp)
            }
        }
    }
}

// Reemplazar FilterChip con botones simples
@Composable
private fun SimpleFilterButtons(
    selectedFilter: AlertFilter,
    onFilterChange: (AlertFilter) -> Unit,
    alertCounts: Map<AlertFilter, Int>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlertFilter.values().forEach { filter ->
            val isSelected = selectedFilter == filter
            val count = alertCounts[filter] ?: 0

            Button(
                onClick = { onFilterChange(filter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) AppColors.Primary else AppColors.Surface,
                    contentColor = if (isSelected) AppColors.OnPrimary else AppColors.OnSurface
                ),
                shape = RoundedCornerShape(20.dp),
                border = if (!isSelected) BorderStroke(1.dp, AppColors.Border) else null,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (filter) {
                            AlertFilter.ALL -> Icons.Outlined.Notifications
                            AlertFilter.TODAY -> Icons.Outlined.Today
                            AlertFilter.CRITICAL -> Icons.Outlined.Warning
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        filter.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                    if (count > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected)
                                        AppColors.OnPrimary.copy(alpha = 0.3f)
                                    else
                                        AppColors.Primary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AppColors.Primary else AppColors.Primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernAlertCard(
    alert: String,
    onClick: () -> Unit
) {
    // Extraer información de la alerta
    val parts = alert.split(" - ")
    val message = parts.firstOrNull() ?: alert
    val timestamp = parts.lastOrNull() ?: ""

    val isCritical = message.contains("salió", ignoreCase = true) ||
            message.contains("crítico", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical)
                AppColors.Error.copy(alpha = 0.05f)
            else
                AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (isCritical)
                AppColors.Error.copy(alpha = 0.2f)
            else
                AppColors.Border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de severidad
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isCritical)
                            AppColors.Error.copy(alpha = 0.15f)
                        else
                            AppColors.Success.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCritical) Icons.Outlined.Error else Icons.Outlined.CheckCircle,
                    contentDescription = "Severidad",
                    tint = if (isCritical) AppColors.Error else AppColors.Success,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido de la alerta
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCritical) "Alerta Crítica" else "Notificación",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCritical) AppColors.Error else AppColors.Success
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface,
                    lineHeight = 20.sp
                )

                if (timestamp.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timestamp,
                        fontSize = 12.sp,
                        color = AppColors.Muted
                    )
                }
            }

            // Indicador de nueva alerta (opcional)
            if (isCritical) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.Error)
                )
            }
        }
    }
}

@Composable
private fun EmptyAlertsState(
    filter: AlertFilter,
    onTestAlert: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustración
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppColors.Primary.copy(alpha = 0.1f),
                            AppColors.Primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (filter) {
                    AlertFilter.ALL -> Icons.Outlined.NotificationsNone
                    AlertFilter.TODAY -> Icons.Outlined.EventNote
                    AlertFilter.CRITICAL -> Icons.Outlined.Warning
                },
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (filter) {
                AlertFilter.ALL -> "Sin alertas"
                AlertFilter.TODAY -> "Sin alertas hoy"
                AlertFilter.CRITICAL -> "Sin alertas críticas"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (filter) {
                AlertFilter.ALL -> "Las alertas aparecerán aquí cuando el dispositivo detecte eventos importantes"
                AlertFilter.TODAY -> "No hay alertas registradas para el día de hoy"
                AlertFilter.CRITICAL -> "No hay alertas críticas en este momento"
            },
            fontSize = 14.sp,
            color = AppColors.Muted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de prueba (solo para filtro ALL)
        if (filter == AlertFilter.ALL) {
            Button(
                onClick = onTestAlert,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.BugReport,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generar alerta de prueba")
            }
        }
    }
}

private enum class AlertFilter(val displayName: String) {
    ALL("Todas"),
    TODAY("Hoy"),
    CRITICAL("Críticas")
}