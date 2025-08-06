package com.zurie.pecuadexproject.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.components.SideBarLayout
import com.zurie.pecuadexproject.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrincipalScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }

    val animalViewModel: AnimalViewModel = viewModel()
    val animalState = animalViewModel.state

    // Calcular estadísticas
    val animales = animalState.animales
    val cantidadTotal = animales.size
    val cantidadAdultos = animales.count { !it.esCria() }
    val cantidadCrias = animales.count { it.esCria() }
    val cantidadCriticos = animales.count { it.critico }

    LaunchedEffect(Unit) {
        animalViewModel.obtenerAnimales()
    }

    BackHandler(enabled = isMenuVisible) {
        isMenuVisible = false
    }

    SideBarLayout(
        isMenuVisible = isMenuVisible,
        onMenuToggle = { isMenuVisible = it },
        currentRoute = "principal",
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con botón de menú mejorado
                ModernHeader(
                    onMenuClick = { isMenuVisible = !isMenuVisible },
                    isMenuVisible = isMenuVisible
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjetas de estadísticas mejoradas
                StatsSection(
                    totalAnimals = cantidadTotal,
                    adults = cantidadAdultos,
                    youngAnimals = cantidadCrias,
                    criticalAnimals = cantidadCriticos,
                    onStatClick = { /* Navigate to detail */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Accesos rápidos
                QuickActionsSection(navController)

                Spacer(modifier = Modifier.height(32.dp))

                // Sección de alertas mejorada
                ImprovedAlertsSection(navController)
            }
        }
    }
}


@Composable
private fun ModernSideMenu(
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxHeight(),
        color = AppColors.Primary,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header del menú
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

            // Items del menú mejorados
            MenuItemCard(
                icon = Icons.Outlined.Dashboard,
                title = "Tablero",
                subtitle = "Vista general",
                isSelected = true,
                onClick = { /* Ya estamos aquí */ }
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
                onClick = { onNavigate("mapa") }
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
private fun ModernHeader(
    onMenuClick: () -> Unit,
    isMenuVisible: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    "Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnBackground
                )
                Text(
                    "Gestión de Ganado",
                    fontSize = 14.sp,
                    color = AppColors.Muted
                )
            }
        }

        // Indicador de estado de conexión
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Success.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.Success)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "En línea",
                    fontSize = 12.sp,
                    color = AppColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    totalAnimals: Int,
    adults: Int,
    youngAnimals: Int,
    criticalAnimals: Int,
    onStatClick: (String) -> Unit
) {
    Column {
        Text(
            "Resumen del Rebaño",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                StatCard(
                    title = "Total",
                    value = totalAnimals.toString(),
                    subtitle = "animales",
                    icon = Icons.Outlined.Pets,
                    color = AppColors.Primary,
                    onClick = { onStatClick("total") }
                )
            }

            item {
                StatCard(
                    title = "Adultos",
                    value = adults.toString(),
                    subtitle = "maduros",
                    icon = Icons.Outlined.LocalFlorist,
                    color = AppColors.Success,
                    onClick = { onStatClick("adults") }
                )
            }

            item {
                StatCard(
                    title = "Crías",
                    value = youngAnimals.toString(),
                    subtitle = "jóvenes",
                    icon = Icons.Outlined.ChildCare,
                    color = AppColors.Warning,
                    onClick = { onStatClick("young") }
                )
            }

            item {
                StatCard(
                    title = "Críticos",
                    value = criticalAnimals.toString(),
                    subtitle = "atención",
                    icon = Icons.Outlined.LocalHospital,
                    color = AppColors.Error,
                    onClick = { onStatClick("critical") }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloatOrNull() ?: 0f,
        animationSpec = tween(1000)
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = animatedValue.toInt().toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = AppColors.Muted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavHostController) {
    Column {
        Text(
            "Acciones Rápidas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                title = "Nuevo Espacio",
                description = "Crear corral",
                icon = Icons.Outlined.Add,
                color = AppColors.Primary,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate("espacios")
            }

            QuickActionCard(
                title = "Ver Mapa",
                description = "Ubicaciones GPS",
                icon = Icons.Outlined.Map,
                color = AppColors.Info,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate("mapa")
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = AppColors.Muted
                )
            }
        }
    }
}

@Composable
private fun ImprovedAlertsSection(navController: NavHostController) {
    var showAlerts by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Alertas",
                        tint = AppColors.Warning,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Alertas Recientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )
                        Text(
                            "Últimas notificaciones",
                            fontSize = 12.sp,
                            color = AppColors.Muted
                        )
                    }
                }

                Row {
                    TextButton(
                        onClick = { navController.navigate("alertas") }
                    ) {
                        Text(
                            "Ver todas",
                            color = AppColors.Primary,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { showAlerts = !showAlerts },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (showAlerts) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = AppColors.Muted
                        )
                    }
                }
            }

            if (showAlerts) {
                Spacer(modifier = Modifier.height(16.dp))

                // Alerta de ejemplo
                AlertItem(
                    title = "Animal fuera de zona",
                    description = "Vaca #123 detectada fuera del área segura",
                    time = "15 min",
                    severity = AlertSeverity.HIGH
                )

                Spacer(modifier = Modifier.height(12.dp))

                AlertItem(
                    title = "Conexión GPS estable",
                    description = "Dispositivo rastreador funcionando correctamente",
                    time = "2 horas",
                    severity = AlertSeverity.LOW
                )
            }
        }
    }
}

@Composable
private fun AlertItem(
    title: String,
    description: String,
    time: String,
    severity: AlertSeverity
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (severity) {
                        AlertSeverity.HIGH -> AppColors.Error.copy(alpha = 0.15f)
                        AlertSeverity.MEDIUM -> AppColors.Warning.copy(alpha = 0.15f)
                        AlertSeverity.LOW -> AppColors.Success.copy(alpha = 0.15f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (severity) {
                    AlertSeverity.HIGH -> Icons.Outlined.Error
                    AlertSeverity.MEDIUM -> Icons.Outlined.Warning
                    AlertSeverity.LOW -> Icons.Outlined.CheckCircle
                },
                contentDescription = "Severidad",
                tint = when (severity) {
                    AlertSeverity.HIGH -> AppColors.Error
                    AlertSeverity.MEDIUM -> AppColors.Warning
                    AlertSeverity.LOW -> AppColors.Success
                },
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = AppColors.Muted,
                lineHeight = 16.sp
            )
        }

        Text(
            text = time,
            fontSize = 11.sp,
            color = AppColors.Muted
        )
    }
}

private enum class AlertSeverity {
    HIGH, MEDIUM, LOW
}