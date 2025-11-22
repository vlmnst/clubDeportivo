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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.graphics.toColorInt

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

        setupNavigationDrawer()

        val txtMonto: TextView = findViewById(R.id.txt_Monto_Pagar)
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)
        val editTitular: EditText = findViewById(R.id.TitularTarjeta)
        val editDNI: EditText = findViewById(R.id.DNITitular)
        val editNumeroTarjeta: EditText = findViewById(R.id.NumeroTarjeta)
        val editFecha: EditText = findViewById(R.id.FechaVencimiento)
        val editCVV: EditText = findViewById(R.id.CVVTarjeta)

        // Traer monto de la BD
        val db = BDatos(this)
        val cuotaSocio = db.obtenerMontoServicio("Cuota Socio")
        txtMonto.text = "$$cuotaSocio"
        db.close()

        // Botón empieza deshabilitado
        btnRegistrarPago.isEnabled = false
        btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())

        fun actualizarEstadoBoton() {
            val titularOk = editTitular.text.toString().trim().isNotEmpty()
            val dniOk = editDNI.text.toString().trim().isNotEmpty()
            val tarjetaOk = editNumeroTarjeta.text.toString().trim().length == 16
            val fechaOk = editFecha.text.toString().trim().isNotEmpty()
            val cvvOk = editCVV.text.toString().trim().length in 3..4

            val datosCompletos = titularOk && dniOk && tarjetaOk && fechaOk && cvvOk

            btnRegistrarPago.isEnabled = datosCompletos
            val color = if (datosCompletos) "#0066CC" else "#BDBDBD"
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
        }

        // TextWatcher para todos los campos
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
            AlertDialog.Builder(this)
                .setTitle("Cobro cuota socios")
                .setMessage("¿Desea registrar el pago?")
                .setPositiveButton("ACEPTAR") { _, _ ->
                    val intent = Intent(this, ConfirmacionCobroSocioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("CANCELAR", null)
                .show()
        }
    }
}
