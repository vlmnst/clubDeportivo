package com.example.clubdeportivo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CustomerManagmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)

        // 1. Encuentra cada selector por su ID
        val selectorCliente = findViewById<SelectorView>(R.id.selector_cliente)
        val selectorCarnet = findViewById<SelectorView>(R.id.selector_carnet)

        selectorCliente.setLabel("Filtrar por cliente:")
        val opcionesCliente = listOf("Todos", "Socio", "No Socio")
        selectorCliente.setOptions(opcionesCliente)

        selectorCarnet.setLabel("Filtrar por carnet:")
        val opcionesCarnet = listOf("Todos", "Con carnet", "Sin Carnet")
        selectorCarnet.setOptions(opcionesCarnet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}