package com.zurie.pecuadexproject.Data.Model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class Animal(
    val idAnimal: Long = 0,
    val apodo: String = "",
    val critico: Boolean = false,
    val fechaFallecimiento: String? = null,
    val fechaNacimiento: String = "", // Formato esperado: "yyyy-MM-dd"
    val peso: Int = 0,
    val sexo: String = "",
    val razaId: Long? = null,
    val espacioId: Long? = null
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun esCria(): Boolean {
        return try {
            // Intentar primero con formato yyyy-MM-dd
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nacimiento = try {
                LocalDate.parse(fechaNacimiento, formatter)
            } catch (e: Exception) {
                // Si falla, intentar con formato dd/MM/yyyy
                val altFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LocalDate.parse(fechaNacimiento, altFormatter)
            }

            val hoy = LocalDate.now()
            val edad = Period.between(nacimiento, hoy).years
            edad < 2
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerEdad(): Int {
        return try {
            // Intentar primero con formato yyyy-MM-dd
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nacimiento = try {
                LocalDate.parse(fechaNacimiento, formatter)
            } catch (e: Exception) {
                // Si falla, intentar con formato dd/MM/yyyy
                val altFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LocalDate.parse(fechaNacimiento, altFormatter)
            }

            val hoy = LocalDate.now()
            Period.between(nacimiento, hoy).years
        } catch (e: Exception) {
            -1
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerCritico(): Boolean {
        return critico
    }
}