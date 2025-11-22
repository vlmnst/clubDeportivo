package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CobroActividadNoSocioActivity : BaseActivity() {

    // Variable para guardar qué seleccionó el usuario (actividad)
    private var servicioSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_actividad_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroActividadNoSocioActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        //C onfigurar el SPINNER
        val spinner: Spinner = findViewById(R.id.SpinnerActividades)

        // ---Conecta con la DB ---
        val db = BDatos(this)
        val listaServicios = db.obtenerListaServicios() // Llamamos a la nueva función

        // Creamos el adaptador con la lista que trajo de la DB
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaServicios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Detectar qué selecciona el usuario
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    servicioSeleccionado = "" // "Servicios disponibles" no cuenta
                } else {
                    servicioSeleccionado = listaServicios[position]
                    // Opcional: Toast.makeText(applicationContext, "Elegiste: $servicioSeleccionado", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 3. Configurar el BOTÓN DE PAGO
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)

        btnRegistrarPago.setOnClickListener {
            if (servicioSeleccionado.isEmpty()) {
                Toast.makeText(this, "Por favor seleccioná un servicio", Toast.LENGTH_SHORT).show()
            } else {
                mostrarConfirmacionPago(spinner)
            }
        }
    }

    private fun mostrarConfirmacionPago(spinner: Spinner) {
        val simpleDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Cobro Actividad No Socio")
            .setMessage("¿Desea registrar el pago de: $servicioSeleccionado?")
            .setPositiveButton("ACEPTAR") { dialog, which ->

                Toast.makeText(this, "Pago acreditado correctamente", Toast.LENGTH_LONG).show()

                // Reiniciar el spinner
                spinner.setSelection(0)
                servicioSeleccionado = ""

                dialog.dismiss()
            }
            .setNegativeButton("CANCELAR") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        simpleDialog.show()
    }
}
