package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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


        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)

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

        val radioGroup: RadioGroup = findViewById(R.id.radioGroupPago)
        radioGroup.setOnCheckedChangeListener { group,checkedId ->
            when (checkedId) {
                R.id.btnTarjeta -> {
                    val intent = Intent(this, CobroCuotaSocioTarjetaActivity::class.java)
                    startActivity(intent)
                }
                R.id.btnEfectivo -> {

                }
            }
        }


    }
}