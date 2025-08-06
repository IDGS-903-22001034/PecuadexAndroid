package com.zurie.pecuadexproject.View

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zurie.pecuadexproject.R
import com.zurie.pecuadexproject.ui.theme.AppColors

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Animaciones corregidas
    val infiniteTransition = rememberInfiniteTransition(label = "loginAnimation")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    val backgroundShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundShimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AppColors.Primary.copy(alpha = 0.1f + backgroundShimmer * 0.05f),
                        AppColors.Secondary.copy(alpha = 0.05f),
                        AppColors.Background
                    ),
                    radius = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animado mejorado
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = AppColors.Primary.copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AppColors.Surface,
                                AppColors.Primary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vaca),
                    contentDescription = "PecuaDex Logo",
                    tint = AppColors.Primary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título y subtítulo mejorados
            Text(
                text = "PecuaDex",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Gestión Inteligente de Ganado",
                fontSize = 16.sp,
                color = AppColors.Secondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            Text(
                text = "Monitoreo GPS • Alertas en Tiempo Real • Control Total",
                fontSize = 12.sp,
                color = AppColors.Muted,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Formulario mejorado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = AppColors.Primary.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                border = BorderStroke(
                    1.dp,
                    AppColors.Primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                        "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Campo de email mejorado
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("ejemplo@utleon.edu.mx") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = "Email",
                                tint = AppColors.Primary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Primary,
                            unfocusedBorderColor = AppColors.Border,
                            focusedLabelColor = AppColors.Primary,
                            focusedLeadingIconColor = AppColors.Primary,
                            unfocusedLeadingIconColor = AppColors.Muted,
                            focusedContainerColor = AppColors.Primary.copy(alpha = 0.02f),
                            unfocusedContainerColor = AppColors.Background
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Campo de contraseña mejorado
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = "Password",
                                tint = AppColors.Primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Outlined.Visibility
                                    else
                                        Icons.Outlined.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "Ocultar contraseña"
                                    else
                                        "Mostrar contraseña",
                                    tint = AppColors.Muted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Primary,
                            unfocusedBorderColor = AppColors.Border,
                            focusedLabelColor = AppColors.Primary,
                            focusedLeadingIconColor = AppColors.Primary,
                            unfocusedLeadingIconColor = AppColors.Muted,
                            focusedContainerColor = AppColors.Primary.copy(alpha = 0.02f),
                            unfocusedContainerColor = AppColors.Background
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkbox recordarme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AppColors.Primary,
                                uncheckedColor = AppColors.Muted
                            )
                        )
                        Text(
                            "Recordar mis credenciales",
                            fontSize = 14.sp,
                            color = AppColors.OnSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Botón de login mejorado
                    Button(
                        onClick = {
                            isLoading = true
                            if (email == "admin@utleon.edu.mx" && password == "password") {
                                navController.navigate("principal") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = AppColors.Primary.copy(alpha = 0.6f)
                        ),
                        enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AppColors.OnPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Iniciando sesión...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.OnPrimary
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Iniciar Sesión",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.OnPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Card de credenciales de prueba mejorada
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Info.copy(alpha = 0.08f)
                ),
                border = BorderStroke(
                    1.dp,
                    AppColors.Info.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.Info.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = AppColors.Info,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Credenciales de Demostración",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Info
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Usuario: admin@utleon.edu.mx",
                            fontSize = 13.sp,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Contraseña: password",
                            fontSize = 13.sp,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer con información de seguridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Security,
                    contentDescription = null,
                    tint = AppColors.Success,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Conexión segura y datos protegidos",
                    fontSize = 12.sp,
                    color = AppColors.Muted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Versión de la app
            Text(
                text = "PecuaDex v1.0 • Hecho con ❤️ para ganaderos",
                fontSize = 11.sp,
                color = AppColors.Muted.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}