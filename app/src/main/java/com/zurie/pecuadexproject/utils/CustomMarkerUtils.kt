package com.zurie.pecuadexproject.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object CustomMarkerUtils {

    // Marcador para el animal real (con GPS físico)
    fun createRealAnimalMarker(context: Context, size: Int = 140): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fondo con borde dorado (para indicar que es el animal real)
        val backgroundPaint = Paint().apply {
            color = Color(0xFF059669).toArgb() // Verde
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color(0xFFFFD700).toArgb() // Dorado
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 12f // Borde más grueso
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 15f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.35f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐄", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        // Añadir indicador "REAL" pequeño
        val realPaint = Paint().apply {
            color = Color(0xFFFFD700).toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.1f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("REAL", centerX, centerY + radius - 5, realPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para animales en peligro (fuera de geocerca)
    fun createDangerAnimalMarker(context: Context, size: Int = 140): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFFDC2626).toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 12f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.35f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐄", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para vacas simuladas
    fun createAnimalMarker(context: Context, size: Int = 120): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFF059669).toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 10f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.4f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐄", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para toros
    fun createBullMarker(context: Context, size: Int = 130): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFF7C2D12).toArgb() // Marrón oscuro
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 10f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.4f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐂", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para crías
    fun createCalfMarker(context: Context, size: Int = 100): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFFF59E0B).toArgb() // Amarillo/naranja
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 8f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.45f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐮", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para ovejas
    fun createSheepMarker(context: Context, size: Int = 110): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFFE5E7EB).toArgb() // Gris claro
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color(0xFF374151).toArgb() // Gris oscuro
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 10f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color(0xFF374151).toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.4f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐑", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador para cabras
    fun createGoatMarker(context: Context, size: Int = 115): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFF8B5CF6).toArgb() // Púrpura
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 10f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.4f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🐐", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Marcador genérico para el centro de geocerca
    fun createGeofenceCenterMarker(context: Context, size: Int = 80): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = Color(0xFF3B82F6).toArgb() // Azul
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val radius = (size / 2f) - 8f

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)

        val iconPaint = Paint().apply {
            color = Color.White.toArgb()
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = size * 0.5f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("🎯", centerX, centerY + (iconPaint.textSize / 3), iconPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}