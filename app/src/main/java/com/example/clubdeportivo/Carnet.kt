package com.example.clubdeportivo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Carnet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        // Esto ajusta los bordes del layout para pantallas modernas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idImgCompartir = findViewById<ImageButton>(R.id.imgCompartir)
        val idImgDescargar = findViewById<ImageButton>(R.id.imgDescargar)

        idImgCompartir.setOnClickListener {
            val intent = Intent(this, Compartir::class.java)
            startActivity(intent)
        }

        idImgDescargar.setOnClickListener {
            Toast.makeText(this, "La descarga ha comenzado en segundo plano.", Toast.LENGTH_LONG).show()
        }

    }
}