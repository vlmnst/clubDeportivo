package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.graphics.toColorInt
import java.time.LocalDate
import java.util.Date
import kotlin.math.log


class RegistrarCliente : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_cliente)

        val dbHelper = BDatos(this)

        // OBTENGO DATOS DEL FORM
        val etNombre = findViewById<EditText>(R.id.edit_nombre_apellido)
        val etDNI = findViewById<EditText>(R.id.edit_dni)
        val etTelefono = findViewById<EditText>(R.id.edit_telefono)
        val etEmail = findViewById<EditText>(R.id.edit_correo_electronico)
        val chApto = findViewById<CheckBox>(R.id.check_apto_medico)
        val raSocio = findViewById<RadioButton>(R.id.btnSocio)
        val raNoSocio = findViewById<RadioButton>(R.id.btnNoSocio)
        val grupoSocio = findViewById<RadioGroup>(R.id.grupoSocio)

        // OBTENGO BOTON REGISTRAR
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.isEnabled = false
        btnRegistrar.backgroundTintList = ColorStateList.valueOf("#BDBDBD".toColorInt())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun actualizarEstadoBoton() {
            val nombreCheck = etNombre.text.toString().isNotEmpty()
            val dniCheck = etDNI.text.toString().isNotEmpty()
            val telCheck = etTelefono.text.toString().isNotEmpty()
            val emailCheck = etEmail.text.toString().isNotEmpty()
            val aptoCheck = chApto.isChecked
            val socioCheck = raSocio.isChecked || raNoSocio.isChecked

            val registroHabilitado = nombreCheck && dniCheck && telCheck && emailCheck && aptoCheck && socioCheck

            btnRegistrar.isEnabled = registroHabilitado

            val color = if (registroHabilitado)
                "#0066CC".toColorInt()
            else
                "#BDBDBD".toColorInt()
            btnRegistrar.backgroundTintList = ColorStateList.valueOf(color)
        }


        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()


        // CLICK EN EL BOTÓN REGISTRAR
        btnRegistrar.setOnClickListener {
            // CREAR NUEVO CLIENTE COMO CLASE
            val nombre = etNombre.text.toString()
            val dniCheck = etDNI.text.toString()
            val telCheck = etTelefono.text.toString()
            val emailCheck = etEmail.text.toString()
            val aptoCheck = chApto.isChecked
            val fechaInscripcion = LocalDate.now().toString()
            val isSocio = if (raSocio.isChecked) true else false
            val nuevoCliente: Cliente = Cliente(
                null, nombre,dniCheck, telCheck, emailCheck, aptoCheck, fechaInscripcion, isSocio, carnet = false )
            // REGISTRO DE NUEVO CLIENTE
            dbHelper.agregarCliente(nuevoCliente)
            // LIMPIO LOS CAMPOS COMPLETADOS
            etNombre.text.clear()
            etDNI.text.clear()
            etTelefono.text.clear()
            etEmail.text.clear()
            chApto.isChecked = false
            grupoSocio.clearCheck()
            // Crear el Intent == "intención" de hacer algo (abrir DashboardActivity)
            val intent = Intent(this, RegistroExitoso::class.java)
            //Pasa los datos del nuevo cliente a la vista RegistroExitoso
            intent.putExtra("nombre", nombre)
            //Corrobora si se trata de Socio o NoSocio y pasa valor del tipo de cliente a la vista RegistroExitoso
            if(isSocio) {
                val socio = raSocio.text.toString()
                intent.putExtra("socio", socio)
            }
            else {
                val socio = raNoSocio.text.toString()
                intent.putExtra("socio", socio)
            }
            // Iniciar la Activity
            startActivity(intent)
        }

        //Validacion para activar el botón de registro
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarEstadoBoton()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etNombre.addTextChangedListener(watcher)
        etDNI.addTextChangedListener(watcher)
        etTelefono.addTextChangedListener(watcher)
        etEmail.addTextChangedListener(watcher)
        chApto.setOnCheckedChangeListener { _, _ -> actualizarEstadoBoton() }
        raSocio.setOnCheckedChangeListener { _, _ -> actualizarEstadoBoton() }
        raNoSocio.setOnCheckedChangeListener { _, _ -> actualizarEstadoBoton() }

    }




}