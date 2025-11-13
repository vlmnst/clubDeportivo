package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

        // Inicializar la DB (crea tablas y usuario admin si no existe)
        val dbHandler = BDatos(this)
        dbHandler.writableDatabase.close()

        // IdS
        val editUsuario = findViewById<EditText>(R.id.et_nombre_usuario)
        val editPass = findViewById<EditText>(R.id.et_contrasenia)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // listener btn login
        btnLogin.setOnClickListener {
            // obtener texto (trim() quita espacios)
            val usuario = editUsuario.text.toString().trim()
            val clave = editPass.text.toString().trim()

            // Check campos NO vacios
            if (usuario.isNotBlank() && clave.isNotBlank()) {

                // Llama funcion verificar usuario de la db (clase)
                val esValido = dbHandler.verificarUsuario(usuario, clave)

                if (esValido) {
                    // si existe usuario
                    Toast.makeText(this, "¡Bienvenido, $usuario!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuPrincipal::class.java)
                    startActivity(intent)
                    finish() // Cierra el MainActivity para que no se pueda volver atrás
                } else {
                    // si no es encuentra el usuario
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                }

            } else {
                // campos vacios
                Toast.makeText(this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show()
            }
        }










    }
}