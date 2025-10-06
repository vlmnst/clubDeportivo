package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obter ID del Btn
        val btnRegistrarCliente = findViewById<Button>(R.id.btn_Registrar_Cliente)
        // listen click == addEventListener('click')
        btnRegistrarCliente.setOnClickListener {
            // Crear el Intent == "intención" de hacer algo (abrir DashboardActivity)
            val intent = Intent(this, RegistrarCliente::class.java)
            // Iniciar la Activity
            startActivity(intent)
        }


        val btnManageCustomer = findViewById<Button>(R.id.btn_manage_customer)
        btnManageCustomer.setOnClickListener {
            val intent = Intent(this, CustomerManagmentActivity::class.java)
            startActivity(intent)
        }

    }
}