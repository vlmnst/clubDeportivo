package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroExitoso : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_exitoso)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        //Obtener ID del tvSocioNoSocio
        val tvSocioNoSocio = findViewById<TextView>(R.id.tvregistroSocioNosocio)
        //Obtener ID del tvNombreApellido
        val tvNombreApellido = findViewById<TextView>(R.id.tvregistroNombreApellido)
        //Obtener ID del Btn Cobrar couta o actividad
        val btnCobrar = findViewById<Button>(R.id.btn_cobrar)

        //Modifica el valor del tvSocioNoSocio con lo que pasa el registro del cliente
        val socio = intent.getStringExtra("socio") ?: "Socio o No Socio" //Si el valor que me llega por parametro "socio" es nulo, pone el String "Socio o No Socio"
        tvSocioNoSocio.text = "$socio"

        //Modifica el valor del tvNombreApellido con lo que pasa el registro del cliente
        val usuario = intent.getStringExtra("nombre") ?: "Usuario" //Si el valor que me llega por parametro "usuario" es nulo, pone el String "Usuario"
        val dni = intent.getStringExtra("DNI")
        tvNombreApellido.text = "$usuario"

        val cuotaActividad = tvSocioNoSocio.text.toString()

        //Compara y personaliza el btn_Cobrar acorde si se trata de Socio o No Socio
        if(cuotaActividad == "Socio") {
            val cobrar = "COBRAR CUOTA"
            btnCobrar.text = cobrar
            // listen click == addEventListener('click')
            btnCobrar.setOnClickListener {
                // Crea el Intent y realiza la conexion con la vista CobroCuotaSocio
                val intent = Intent(this, CobroCuotaSocio::class.java)
                intent.putExtra("DNI", dni)
                startActivity(intent)
            }
        }
        else{
            val cobrar = "COBRAR ACTIVIDAD"
            btnCobrar.text = cobrar
            // listen click == addEventListener('click')
            btnCobrar.setOnClickListener {
                // Crea el Intent y realiza la conexion con la vista CobroActividadNoSocioActivity
                val intent = Intent(this, CobroActividadNoSocioActivity::class.java)
                // Iniciar la Activity
                startActivity(intent)
            }
        }


    }

}