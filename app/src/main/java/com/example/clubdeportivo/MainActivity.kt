package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obter ID del Btn
        val btnLogin = findViewById<Button>(R.id.btn_login)
        // listen click == addEventListener('click')
        btnLogin.setOnClickListener {
            // Crear el Intent == "intención" de hacer algo (abrir DashboardActivity)
            val intent = Intent(this, MenuPrincipal::class.java)
            // Iniciar la Activity
            startActivity(intent)
        }
    }
}