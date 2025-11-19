package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class ConfirmacionCobroSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_cobro_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nombreCompleto = intent.getStringExtra("nombreCompleto")
        val esSocio = intent.getBooleanExtra("esSocio", false)
        val fechaDePago = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val nuevaFechaVto = fechaDePago.format(calendar.time)

        val textoConfirmacion = findViewById<TextView>(R.id.tvCobroExitoso)
        var textoFechaVto = findViewById<TextView>(R.id.tvNuevaFechaVencimiento)

        "El ${ if (esSocio) "socio" else "no socio" }  $nombreCompleto realizó el pago de su ${ if (esSocio) "Cuota" else "Actividad" } correctamente"
            .also { textoConfirmacion.text = it }

        if(esSocio) {
            "Su nueva fecha de vencimiento es: $nuevaFechaVto".also { textoFechaVto.text = it }
            textoFechaVto.visibility = View.VISIBLE
        } else {
            textoFechaVto.visibility = View.GONE
        }

        // Volver a la gestión de cliente
        val btnVolver = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_volverGestion)
        btnVolver.setOnClickListener {
                    //Va a una nueva pantalla
                    val intent = Intent(this, CustomerManagmentActivity::class.java)
                    startActivity(intent)
        }
    }
}