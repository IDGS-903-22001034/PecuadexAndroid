package com.zurie.pecuadexproject.View

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.Data.Model.Espacio
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ViewModels.AnimalViewModel
import com.zurie.pecuadexproject.ViewModels.EspacioViewModel
import com.zurie.pecuadexproject.components.SideBarLayout
import com.zurie.pecuadexproject.ui.theme.AppColors

@Composable
fun ListaEspaciosScreen(
    navController: NavHostController,
    espacioViewModel: EspacioViewModel = viewModel(),
    animalViewModel: AnimalViewModel = viewModel(),
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filtroActivo by remember { mutableStateOf(true) }
    var sortBy by remember { mutableStateOf(SortType.NAME) }

    val estado = espacioViewModel.state
    val animalState = animalViewModel.state

    LaunchedEffect(Unit) {
        espacioViewModel.obtenerEspacios()
        animalViewModel.obtenerAnimales()
    }

    BackHandler(enabled = isMenuVisible) {
        isMenuVisible = false
    }

    val espaciosFiltrados = remember(estado.espacios, searchQuery, filtroActivo, sortBy) {
        estado.espacios
            .filter { it.estatus == filtroActivo }
            .filter {
                if (searchQuery.isBlank()) true
                else it.nombre.contains(searchQuery, ignoreCase = true) ||
                        it.direccion.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(
                when (sortBy) {
                    SortType.NAME -> compareBy { it.nombre }
                    SortType.ANIMALS -> compareByDescending { espacio ->
                        animalState.animales.count { it.espacioId?.toLong() == espacio.idEspacio }
                    }
                    SortType.STATUS -> compareBy { it.estatus }
                }
            )
    }

    SideBarLayout(
        isMenuVisible = isMenuVisible,
        onMenuToggle = { isMenuVisible = it },
        currentRoute = "listaEspacios",
        onNavigate = { route -> navController.navigate(route) }
    ) {
        // Contenedor principal como Box para superponer menú y FAB
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
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ImprovedHeader(
                    title = "Espacios",
                    subtitle = "${espaciosFiltrados.size} espacios encontrados",
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onMenuClick = { isMenuVisible = !isMenuVisible },
                    isMenuVisible = isMenuVisible
                )

                Spacer(modifier = Modifier.height(20.dp))

                FilterSection(
                    filtroActivo = filtroActivo,
                    onFilterChange = { filtroActivo = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    estado.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingSpinner()
                        }
                    }
                    estado.error != null -> {
                        ErrorState(
                            error = estado.error,
                            onRetry = { espacioViewModel.obtenerEspacios() }
                        )
                    }
                    espaciosFiltrados.isEmpty() -> {
                        EmptyState(
                            isFiltered = searchQuery.isNotBlank() || !filtroActivo,
                            onAddSpace = { navController.navigate("espacios") },
                            onClearFilters = {
                                searchQuery = ""
                                filtroActivo = true
                            }
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(espaciosFiltrados) { espacio ->
                                val cantidadAnimales = animalState.animales.count {
                                    it.espacioId?.toLong() == espacio.idEspacio
                                }

                                ModernEspacioCard(
                                    espacio = espacio,
                                    cantidadAnimales = cantidadAnimales,
                                    onClick = {
                                        navController.navigate("detalleEspacio/${espacio.idEspacio}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Menú lateral superpuesto a la izquierda
            AnimatedVisibility(
                visible = isMenuVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Color.Transparent) // Para no bloquear clicks fuera del menú
            ) {
                ModernSideMenu(
                    currentRoute = "listaEspacios",
                    onNavigate = { route ->
                        navController.navigate(route)
                        isMenuVisible = false
                    },
                    onClose = { isMenuVisible = false }
                )
            }

            // FAB posicionado abajo a la derecha
            FloatingActionButton(
                onClick = { navController.navigate("espacios") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = AppColors.Primary,
                contentColor = AppColors.OnPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar espacio",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
private fun ImprovedHeader(
    title: String,
    subtitle: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMenuClick: () -> Unit,
    isMenuVisible: Boolean
) {
    Column {
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
                        title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnBackground
                    )
                    Text(
                        subtitle,
                        fontSize = 14.sp,
                        color = AppColors.Muted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Barra de búsqueda mejorada
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Buscar espacios...",
                    color = AppColors.Muted
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = AppColors.Primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = AppColors.Muted
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.Border,
                focusedContainerColor = AppColors.Surface,
                unfocusedContainerColor = AppColors.Surface
            ),
            singleLine = true
        )
    }
}

@Composable
private fun FilterSection(
    filtroActivo: Boolean,
    onFilterChange: (Boolean) -> Unit,
    sortBy: SortType,
    onSortChange: (SortType) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filtros de estado
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtroActivo,
                onClick = { onFilterChange(true) },
                label = {
                    Text(
                        "Activos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = if (filtroActivo) {
                    {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )

            FilterChip(
                selected = !filtroActivo,
                onClick = { onFilterChange(false) },
                label = {
                    Text(
                        "Inactivos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = if (!filtroActivo) {
                    {
                        Icon(
                            Icons.Outlined.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    {
                        Icon(
                            Icons.Outlined.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        }

        // Botón de ordenamiento (sin cambios)
        Box {
            OutlinedButton(
                onClick = { showSortMenu = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.Primary
                ),
                border = BorderStroke(1.dp, AppColors.Border),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = "Ordenar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    when (sortBy) {
                        SortType.NAME -> "Nombre"
                        SortType.ANIMALS -> "Animales"
                        SortType.STATUS -> "Estado"
                    },
                    fontSize = 14.sp
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortType.values().forEach { sort ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (sort) {
                                    SortType.NAME -> "Por nombre"
                                    SortType.ANIMALS -> "Por cantidad de animales"
                                    SortType.STATUS -> "Por estado"
                                }
                            )
                        },
                        onClick = {
                            onSortChange(sort)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (sortBy == sort) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernEspacioCard(
    espacio: Espacio,
    cantidadAnimales: Int,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (cantidadAnimales > 0) 1f else 0.95f,
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            1.dp,
            if (espacio.estatus) AppColors.Border else AppColors.Error.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header de la card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icono del espacio
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.Primary.copy(alpha = 0.1f),
                                        AppColors.Secondary.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.establo),
                            contentDescription = "Espacio",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = espacio.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = espacio.direccion,
                            fontSize = 14.sp,
                            color = AppColors.Muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Raza asignada
                        espacio.raza?.let { raza ->
                            Text(
                                text = "Raza: ${raza.nombre}",
                                fontSize = 12.sp,
                                color = AppColors.Secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Contador de animales
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                cantidadAnimales == 0 -> AppColors.Gray200
                                cantidadAnimales < 5 -> AppColors.Warning.copy(alpha = 0.15f)
                                else -> AppColors.Success.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$cantidadAnimales",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                cantidadAnimales == 0 -> AppColors.Muted
                                cantidadAnimales < 5 -> AppColors.Warning
                                else -> AppColors.Success
                            }
                        )
                        Text(
                            text = if (cantidadAnimales == 1) "animal" else "animales",
                            fontSize = 8.sp,
                            color = AppColors.Muted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer con estado y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado del espacio
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (espacio.estatus) AppColors.Success else AppColors.Error
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (espacio.estatus) "Activo" else "Inactivo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (espacio.estatus) AppColors.Success else AppColors.Error
                    )
                }

                // Indicador de flecha
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = AppColors.Muted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingSpinner() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = AppColors.Primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Cargando espacios...",
            color = AppColors.Muted,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Error,
            contentDescription = "Error",
            tint = AppColors.Error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Error al cargar espacios",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Text(
            error,
            fontSize = 14.sp,
            color = AppColors.Muted,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
private fun EmptyState(
    isFiltered: Boolean,
    onAddSpace: () -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isFiltered) Icons.Outlined.SearchOff else Icons.Outlined.Add,
            contentDescription = "Sin espacios",
            tint = AppColors.Muted,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (isFiltered) "Sin resultados" else "No hay espacios",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Text(
            if (isFiltered)
                "No se encontraron espacios con los filtros aplicados"
            else
                "Crea tu primer espacio para gestionar tu ganado",
            fontSize = 14.sp,
            color = AppColors.Muted,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isFiltered) {
            OutlinedButton(
                onClick = onClearFilters,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.Primary
                ),
                border = BorderStroke(1.dp, AppColors.Primary)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Limpiar filtros")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onAddSpace,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar espacio")
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
                onClick = { /* Ya estamos aquí */ }
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

private enum class SortType {
    NAME, ANIMALS, STATUS
}