package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat // fecha
import java.util.Date //  la fecha
import java.util.Locale //  fecha

class CobroCuotaSocio : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_cuota_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroCuotaSocio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Obtengo el nombre del cliente
        val nombreCompleto = intent.getStringExtra("nombreCompleto")


        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        val txtMonto: TextView = findViewById(R.id.txt_Monto_Pagar)
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)
        // toma dni
        val inputDni: EditText = findViewById(R.id.inputDniSocio)

        //Traer datos de la BD//
        val db = BDatos(this)
        val cuotaSocio = db.obtenerMontoServicio("Cuota Socio")
        txtMonto.text = cuotaSocio
        db.close()

        txtMonto.text = "$$cuotaSocio"

        //Deshabilitar botón//
        btnRegistrarPago.isEnabled = false
        btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())

        var metodoPagoSeleccionado = false


        fun actualizarEstadoBoton() {
            val dniIngresado = inputDni.text.toString().trim().isNotEmpty()

            // si metodo pago Y  DNI escrito then = botón se activa
            val habilitar = metodoPagoSeleccionado && dniIngresado

            btnRegistrarPago.isEnabled = habilitar

            val color = if (habilitar)
                "#0066CC".toColorInt()
            else
                "#BDBDBD".toColorInt()
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(color)
        }

        val radioGroup: RadioGroup = findViewById(R.id.radioGroupPago)
        val form = findViewById<LinearLayout>(R.id.allForm)
        radioGroup.setOnCheckedChangeListener { group,checkedId ->
            when (checkedId) {
                R.id.btnTarjeta -> {
                    form.visibility = View.VISIBLE
                }
                R.id.btnEfectivo -> {
                    form.visibility = View.GONE

                }
            }
        }
        //  texto del DNI
        inputDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                actualizarEstadoBoton()
            }
        })




        //  Registrar Pago EFECTIvo
        btnRegistrarPago.setOnClickListener {
            val simpleDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Cobro cuota socios")
                .setMessage("¿Desea registrar el pago?")
                .setPositiveButton("ACEPTAR") { dialog, which ->

                    // btener datos
                    val dniCliente = inputDni.text.toString().trim()
                    val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // Llamar a la DB
                    val dbPago = BDatos(this)
                    val exito = dbPago.registrarPagoCuota(dniCliente, fechaHoy)

                    if (exito) {
                        Toast.makeText(this, "Pago en efectivo registrado correctamente", Toast.LENGTH_LONG).show()

                        // limpiar campos
                        inputDni.text.clear()
                        radioGroup.clearCheck()
                        metodoPagoSeleccionado = false
                        actualizarEstadoBoton()
                    } else {
                        Toast.makeText(this, "Error: DNI no encontrado en el sistema", Toast.LENGTH_LONG).show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("CANCELAR") { dialog, which ->
                    dialog.dismiss()
                }
                .create()

            simpleDialog.show()
        }
    }
}