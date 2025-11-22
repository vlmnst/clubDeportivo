package com.example.clubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.TextWatcher
import android.text.Editable

class CobroCuotaSocio : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_cuota_socio)

        // Ajustar padding según barras del sistema (notch/status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroCuotaSocio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener DNI del cliente enviado desde la activity anterior
        val dni = intent.getStringExtra("DNI")

        // Configuración del menú lateral
        setupNavigationDrawer()

        // Referencias a vistas
        val txtMonto: TextView = findViewById(R.id.txt_Monto_Pagar)
        val btnRegistrarPago: Button = findViewById(R.id.btnRegistrarPago)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroupPago)
        val formTarjeta: LinearLayout = findViewById(R.id.allForm)

        val editTitular: EditText = findViewById(R.id.TitularTarjeta)
        val editDNI: EditText = findViewById(R.id.DNITitular)
        val editNumeroTarjeta: EditText = findViewById(R.id.NumeroTarjeta)
        val editFecha: EditText = findViewById(R.id.FechaVencimiento)
        val editCVV: EditText = findViewById(R.id.CVVTarjeta)

        // Traer monto de la DB y mostrarlo
        val db = BDatos(this)
        val cuotaSocio = db.obtenerMontoServicio("Cuota Socio")
        db.close()
        txtMonto.text = "$$cuotaSocio"

        // Inicializar botón como deshabilitado
        btnRegistrarPago.isEnabled = false
        btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())

        var metodoPagoSeleccionado = "" // Puede ser "Efectivo" o "Tarjeta"

        // Función que habilita/deshabilita el botón según campos completos en tarjeta
        fun actualizarEstadoBotonTarjeta() {
            val completos = editTitular.text.isNotBlank() &&
                    editDNI.text.isNotBlank() &&
                    editNumeroTarjeta.text.isNotBlank() &&
                    editFecha.text.isNotBlank() &&
                    editCVV.text.isNotBlank()
            btnRegistrarPago.isEnabled = completos
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(
                if (completos) "#0066CC".toColorInt() else "#BDBDBD".toColorInt()
            )
        }

        // TextWatcher que revisa todos los campos de tarjeta al escribir
        val tarjetaTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Solo actualizar si el método seleccionado es tarjeta
                if (metodoPagoSeleccionado == "Tarjeta") {
                    actualizarEstadoBotonTarjeta()
                }
            }
        }

        // Asociar TextWatcher a todos los campos de tarjeta
        editTitular.addTextChangedListener(tarjetaTextWatcher)
        editDNI.addTextChangedListener(tarjetaTextWatcher)
        editNumeroTarjeta.addTextChangedListener(tarjetaTextWatcher)
        editFecha.addTextChangedListener(tarjetaTextWatcher)
        editCVV.addTextChangedListener(tarjetaTextWatcher)

        // Listener para seleccionar método de pago
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btnTarjeta -> {
                    formTarjeta.visibility = View.VISIBLE
                    metodoPagoSeleccionado = "Tarjeta"
                    // Deshabilitar botón hasta que se completen todos los campos
                    btnRegistrarPago.isEnabled = false
                    btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())
                }
                R.id.btnEfectivo -> {
                    formTarjeta.visibility = View.GONE
                    metodoPagoSeleccionado = "Efectivo"
                    // Efectivo habilita automáticamente
                    btnRegistrarPago.isEnabled = true
                    btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#0066CC".toColorInt())
                }
            }
        }

        // Acción del botón registrar pago
        btnRegistrarPago.setOnClickListener {
            // Validación campos tarjeta si se seleccionó tarjeta
            if (metodoPagoSeleccionado == "Tarjeta") {
                val completos = editTitular.text.isNotBlank() &&
                        editDNI.text.isNotBlank() &&
                        editNumeroTarjeta.text.isNotBlank() &&
                        editFecha.text.isNotBlank() &&
                        editCVV.text.isNotBlank()
                if (!completos) {
                    Toast.makeText(this, "Complete todos los campos de tarjeta", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Diálogo de confirmación de pago
            val simpleDialog = AlertDialog.Builder(this)
                .setTitle("Cobro cuota socios")
                .setMessage("¿Desea registrar el pago?")
                .setPositiveButton("ACEPTAR") { dialog, _ ->
                    val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val dbPago = BDatos(this)
                    val exito = dbPago.registrarPagoCuota(dni ?: "", fechaHoy)
                    if (exito) {
                        Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, CustomerManagmentActivity::class.java)
                        startActivity(intent)

                        // Resetear campos y estado
                        radioGroup.clearCheck()
                        formTarjeta.visibility = View.INVISIBLE
                        metodoPagoSeleccionado = ""
                        btnRegistrarPago.isEnabled = false
                        btnRegistrarPago.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())
                        editTitular.text.clear()
                        editDNI.text.clear()
                        editNumeroTarjeta.text.clear()
                        editFecha.text.clear()
                        editCVV.text.clear()
                    } else {
                        Toast.makeText(this, "Error: DNI no encontrado en el sistema", Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("CANCELAR") { dialog, _ -> dialog.dismiss() }
                .create()
            simpleDialog.show()
        }
    }
}
