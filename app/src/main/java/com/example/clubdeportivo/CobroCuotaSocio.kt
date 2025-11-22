package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.TextWatcher
import android.widget.TextView


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

        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        val txtMonto: TextView = findViewById(R.id.txt_Monto_Pagar)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroupPago)
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)

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

        //Actualizo estado del botón
        fun actualizarEstadoBoton() {
            btnRegistrarPago.isEnabled = metodoPagoSeleccionado

            val color = if (metodoPagoSeleccionado)
                "#0066CC".toColorInt()
            else
                "#BDBDBD".toColorInt()
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(color)
        }

        //Detectar cuando eligen metodo de pago//
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            metodoPagoSeleccionado =
                checkedId == R.id.btnTarjeta || checkedId == R.id.btnEfectivo
            actualizarEstadoBoton()

            when (checkedId) {
                R.id.btnTarjeta -> {
                    startActivity(Intent(this, CobroCuotaSocioTarjetaActivity::class.java))
                    radioGroup.clearCheck()
                }
                R.id.btnEfectivo -> {

                }
            }
        }

        //Accion del alert al presionar registrar pago//
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