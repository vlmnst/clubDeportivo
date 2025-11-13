package com.example.clubdeportivo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    val id: Int? = null, // El ID es nulo al insertar, pero se recibe al leer
    val nombre: String,
    val dni: String,
    val telefono: String?, // Nulable si no es obligatorio
    val email: String?,    // Nulable si no es obligatorio
    val apto: Boolean,
    val fecha_inscripcion: String, // Formato "AAAA-MM-DD"
    val socio: Boolean,
    val carnet: Boolean,
) : Parcelable