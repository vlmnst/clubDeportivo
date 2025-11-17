package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class CobroCuotaSocioTarjetaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_cuota_socio_tarjeta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroCuotaSocioTarjetaActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        //Campos referenciados
        val txtMonto: TextView = findViewById(R.id.txt_Monto_Pagar)
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)
        val editTitular: EditText = findViewById(R.id.TitularTarjeta)
        val editDNI: EditText = findViewById(R.id.DNITitular)
        val editNumeroTarjeta: EditText = findViewById(R.id.NumeroTarjeta)
        val editFecha: EditText = findViewById(R.id.FechaVencimiento)
        val editCVV: EditText = findViewById(R.id.CVVTarjeta)

        //Deshabilitar botón//
        btnRegistrarPago.isEnabled = false

        //Traer datos de la BD//
        val db = BDatos(this)
        val cuotaSocio = db.obtenerMontoServicio("Cuota Socio")
        txtMonto.text = cuotaSocio
        db.close()

        txtMonto.text = "$$cuotaSocio"

        fun actualizarEstadoBoton() {
            val datosCompletos = editTitular.text.toString().trim().isNotEmpty() &&
                    editDNI.text.toString().trim().isNotEmpty() &&
                    editNumeroTarjeta.text.toString().trim().isNotEmpty() &&
                    editFecha.text.toString().trim().isNotEmpty() &&
                    editCVV.text.toString().trim().isNotEmpty()

            btnRegistrarPago.isEnabled = datosCompletos

            val color = if (datosCompletos)
                "#0066CC".toColorInt()
            else
                "#BDBDBD".toColorInt()
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(color)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                actualizarEstadoBoton()
            }
        }

        editTitular.addTextChangedListener(watcher)
        editDNI.addTextChangedListener(watcher)
        editNumeroTarjeta.addTextChangedListener(watcher)
        editFecha.addTextChangedListener(watcher)
        editCVV.addTextChangedListener(watcher)

        btnRegistrarPago.setOnClickListener {
            val simpleDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Cobro cuota socios")
                .setMessage("¿Desea registrar el pago?")
                .setPositiveButton("ACEPTAR") { dialog, which ->
                    //Va a una nueva pantalla
                    val intent = Intent(this, ConfirmacionCobroSocioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("CANCELAR") { dialog, which ->
                    dialog.dismiss()
                }
                .create()

            simpleDialog.show()

        }

    }
}