package com.example.clubdeportivo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Compartir : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_compartir)

        // Esto ajusta los bordes del layout para pantallas modernas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val whatsapp = findViewById<LinearLayout>(R.id.idWhatsapp)
        val gmail = findViewById<LinearLayout>(R.id.idGmail)
        val drive = findViewById<LinearLayout>(R.id.idDrive)
        val print = findViewById<LinearLayout>(R.id.idImprimir)

        whatsapp.setOnClickListener {
            Toast.makeText(this, "Se comparte por Whatsapp.", Toast.LENGTH_LONG).show()
        }

        gmail.setOnClickListener {
            Toast.makeText(this, "Se comparte por Gmail.", Toast.LENGTH_LONG).show()
        }

        drive.setOnClickListener {
            Toast.makeText(this, "Se comparte por Drive.", Toast.LENGTH_LONG).show()
        }

        print.setOnClickListener {
            Toast.makeText(this, "Se imprime.", Toast.LENGTH_LONG).show()
        }

    }
}