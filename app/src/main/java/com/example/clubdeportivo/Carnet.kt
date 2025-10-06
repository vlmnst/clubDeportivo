package com.example.clubdeportivo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Carnet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)
        val client = intent.getParcelableExtra<Client>("client_to_print")
        // Esto ajusta los bordes del layout para pantallas modernas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // -----SETEAR LOS DATOS EN EL CARNET -----//
        val nameClient = findViewById<TextView>(R.id.txtNombre)
        nameClient?.text ="${client?.name}"
        val lastNameClient = findViewById<TextView>(R.id.txtApellido)
        lastNameClient?.text = "${client?.lastName}"
        val dniClient = findViewById<TextView>(R.id.tv_dni)
        dniClient?.text = "${client?.dni}"



        //----- BOTONES FUNCIONES -------/
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