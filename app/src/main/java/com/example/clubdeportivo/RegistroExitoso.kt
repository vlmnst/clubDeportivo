package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroExitoso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_exitoso)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Obter ID del BtnRegistrar
        val btnRegistrar = findViewById<Button>(R.id.btn_cobrar)
        // listen click == addEventListener('click')
        btnRegistrar.setOnClickListener {
            // Crear el Intent == "intención" de hacer algo (abrir DashboardActivity)
            val intent = Intent(this, MenuPrincipal::class.java)
            // Iniciar la Activity
            startActivity(intent)
        }
    }

}