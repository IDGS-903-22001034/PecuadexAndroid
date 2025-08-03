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

    fun createDangerAnimalMarker(context: Context, size: Int = 120): BitmapDescriptor {
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
}