package com.zurie.pecuadexproject.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.zIndex
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ui.theme.AppColors

@Composable
fun SideBarLayout(
    isMenuVisible: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        content()

        // Overlay semi-transparente cuando el menú está visible
        if (isMenuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onMenuToggle(false) }
                    .zIndex(1f)
            )
        }

        // Menú lateral superpuesto
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 400)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 400)
            ),
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .zIndex(2f)
        ) {
            ModernSideMenu(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    onNavigate(route)
                    onMenuToggle(false)
                },
                onClose = { onMenuToggle(false) }
            )
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

            // Items del menú
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
                isSelected = currentRoute == "listaEspacios",
                onClick = { onNavigate("listaEspacios") }
            )

            MenuItemCard(
                icon = Icons.Outlined.LocationOn,
                title = "Geocercas",
                subtitle = "Control de ubicación",
                isSelected = currentRoute == "mapa",
                onClick = { onNavigate("mapa") }
            )

            MenuItemCard(
                icon = Icons.Outlined.Warning,
                title = "Alertas",
                subtitle = "Notificaciones",
                isSelected = currentRoute == "alertas",
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