package com.example.clubdeportivo

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.res.ColorStateList
import androidx.core.graphics.toColorInt

class CobroActividadNoSocioActivity : BaseActivity() {

    private var adapter: DatoAdapter? = null
    private var bd: BDatos? = null
    private var actividadSeleccionada: Actividad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobro_actividad_no_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CobroActividadNoSocioActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigationDrawer()

        // Base de datos
        bd = BDatos(this)

        // === RECYCLER VIEW ===
        val rv = findViewById<RecyclerView>(R.id.rvListaActividades)
        rv.layoutManager = LinearLayoutManager(this)
        val listaActividades = obtenerServiciosDesdeBD()

        adapter = DatoAdapter(listaActividades.toMutableList()) { actividad ->
            actividadSeleccionada = actividad
            actualizarEstadoBotones()
        }
        rv.adapter = adapter

        // === BOTONES ===
        val btnSeleccionar = findViewById<Button>(R.id.btnSeleccionar)
        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        val colorDeshabilitado = "#BDBDBD".toColorInt()
        val colorSeleccion = "#56A5D9".toColorInt()
        val colorHabilitado = "#0066CC".toColorInt()

        // Inicialmente deshabilitados
        btnSeleccionar.isEnabled = false
        btnSeleccionar.backgroundTintList = ColorStateList.valueOf(colorDeshabilitado)
        btnRegistrarPago.isEnabled = false
        btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(colorDeshabilitado)

        // === ACCIÓN BOTÓN SELECCIONAR ===
        btnSeleccionar.setOnClickListener {
            if (actividadSeleccionada != null) {
                btnRegistrarPago.isEnabled = true
                btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(colorHabilitado)
                Toast.makeText(this, "Actividad confirmada.", Toast.LENGTH_SHORT).show()
            }
        }

        // === ACCIÓN BOTÓN REGISTRAR PAGO ===
        btnRegistrarPago.setOnClickListener {
            val act = actividadSeleccionada
            if (act == null) {
                Toast.makeText(this, "Seleccione una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar pago")
                .setMessage("¿Registrar el pago de ${act.nombre} por $${act.precio}?")
                .setPositiveButton("Sí") { _, _ ->
                    Toast.makeText(this, "Pago registrado", Toast.LENGTH_LONG).show()
                    // Aquí puedes agregar lógica para guardar en tabla Pagos
                }
                .setNegativeButton("No", null)
                .show()
        }

        // === BOTÓN AGREGAR ===
        btnAgregar.setOnClickListener { mostrarDialogoAgregar() }
    }

    private fun actualizarEstadoBotones() {
        val btnSeleccionar = findViewById<Button>(R.id.btnSeleccionar)
        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)

        val colorDeshabilitado = "#BDBDBD".toColorInt()
        val colorSeleccion = "#56A5D9".toColorInt()
        val colorHabilitado = "#0066CC".toColorInt()

        if (actividadSeleccionada != null) {
            btnSeleccionar.isEnabled = true
            btnSeleccionar.backgroundTintList = ColorStateList.valueOf(colorSeleccion)
        } else {
            btnSeleccionar.isEnabled = false
            btnSeleccionar.backgroundTintList = ColorStateList.valueOf(colorDeshabilitado)
            btnRegistrarPago.isEnabled = false
            btnRegistrarPago.backgroundTintList = ColorStateList.valueOf(colorDeshabilitado)
        }
    }

    // Obtener lista de actividades desde BD
    private fun obtenerServiciosDesdeBD(): MutableList<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = bd?.readableDatabase ?: return lista
        val cursor = db.rawQuery(
            "SELECT * FROM Servicios WHERE tipo_servicio != 'Cuota Socio'",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("tipo_servicio"))
                val precio = cursor.getString(cursor.getColumnIndexOrThrow("monto"))
                lista.add(Actividad(id, nombre, precio))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    private fun mostrarDialogoAgregar() {
        val layout = layoutInflater.inflate(R.layout.dialog_agregar_actividad, null)
        val inputNombre = layout.findViewById<EditText>(R.id.etNombreActividad)
        val inputPrecio = layout.findViewById<EditText>(R.id.etPrecioActividad)

        AlertDialog.Builder(this)
            .setTitle("Nueva actividad")
            .setView(layout)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = inputNombre.text.toString()
                val precio = inputPrecio.text.toString()

                if (nombre.isEmpty() || precio.isEmpty()) {
                    Toast.makeText(this, "Ingrese nombre y precio", Toast.LENGTH_SHORT).show()
                } else {
                    if (bd?.agregarServicio(nombre, precio) == true) {
                        adapter?.agregarActividad(Actividad(0, nombre, precio))
                        Toast.makeText(this, "Actividad agregada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
