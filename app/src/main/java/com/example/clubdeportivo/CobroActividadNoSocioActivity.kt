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

class CobroActividadNoSocioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_actividad_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroActividadNoSocioActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)

        btnRegistrarPago.setOnClickListener {
            val simpleDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Cobro cuota socios")
                .setMessage("¿Desea registrar el pago?")
                .setPositiveButton("ACEPTAR") { dialog, which ->
                    //Va a una nueva pantalla
                    val intent = Intent(this, ConfirmacionCobroActividadActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("CANCELAR") { dialog, which ->
                    dialog.dismiss()
                }
                .create()

            simpleDialog.show()

        }
        val spinner: Spinner = findViewById(R.id.SpinnerActividades)
        val items =
            listOf("Actividades disponibles, Funcional $2500", "Aerobox $3000", "Zumba $3000")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    Toast.makeText(this@CobroActividadNoSocioActivity, "Por favor, seleccioná una actividad", Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val selectedItem = items[position]
                    Toast.makeText(this@CobroActividadNoSocioActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT
                    ).show()


                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }
}
