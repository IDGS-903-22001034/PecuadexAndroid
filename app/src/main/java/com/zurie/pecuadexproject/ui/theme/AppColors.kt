package com.zurie.pecuadexproject.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Colores primarios - Minimalismo elegante
    val Primary = Color(0xFF121212)        // Negro profundo
    val PrimaryVariant = Color(0xFF1C1C1C) // Gris oscuro
    val Secondary = Color(0xFFFFFFFF)      // Blanco puro
    val SecondaryVariant = Color(0xFFF5F5F5) // Blanco grisáceo

    // Colores de fondo
    val Background = Color(0xFFFAFAFA)     // Gris muy claro para fondo
    val Surface = Color(0xFFFFFFFF)        // Blanco puro para superficies
    val SurfaceVariant = Color(0xFFF0F0F0) // Gris claro

    // Estados funcionales
    val Error = Color(0xFFB71C1C)          // Rojo oscuro para errores
    val Success = Color(0xFF388E3C)        // Verde oscuro para éxito
    val Warning = Color(0xFFFBC02D)        // Amarillo mostaza tenue para advertencias
    val Info = Color(0xFF455A64)           // Azul grisáceo para información

    // Colores de texto
    val OnPrimary = Color(0xFFFFFFFF)      // Texto sobre primary (negro) en blanco
    val OnSecondary = Color(0xFF121212)    // Texto sobre blanco en negro
    val OnBackground = Color(0xFF212121)   // Texto sobre fondo claro
    val OnSurface = Color(0xFF212121)      // Texto sobre superficie blanca
    val OnError = Color(0xFFFFFFFF)        // Texto sobre rojo oscuro

    // Colores específicos para la app
    val AnimalHealthy = Success             // Verde oscuro para animales sanos
    val AnimalCritical = Error              // Rojo oscuro para críticos
    val AnimalYoung = Warning               // Amarillo mostaza para crías
    val GeofenceInside = PrimaryVariant    // Gris oscuro para zona segura
    val GeofenceOutside = Error             // Rojo oscuro para fuera de zona

    // Colores neutros
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFEEEEEE)
    val Gray300 = Color(0xFFE0E0E0)
    val Gray400 = Color(0xFFBDBDBD)
    val Gray500 = Color(0xFF9E9E9E)
    val Gray600 = Color(0xFF757575)
    val Gray700 = Color(0xFF616161)
    val Gray800 = Color(0xFF424242)
    val Gray900 = Color(0xFF212121)

    // Colores de estado mejorados
    val Connected = Success
    val Disconnected = Error
    val Inside = GeofenceInside
    val Outside = GeofenceOutside
    val Border = Gray300
    val Muted = Gray500
    val Divider = Gray200

    // Gradientes para cards especiales (más neutro)
    val GradientStart = Gray100
    val GradientEnd = Gray400
}
