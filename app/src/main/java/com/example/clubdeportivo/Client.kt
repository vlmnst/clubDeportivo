package com.example.clubdeportivo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val name: String,
    val lastName: String,
    val dni: String,
    val isPartner: Boolean
) : Parcelable