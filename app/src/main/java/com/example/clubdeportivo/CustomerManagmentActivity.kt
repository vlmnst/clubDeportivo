package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerManagmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)

        // --- SELECTORES ----- //
        val selectorCliente = findViewById<SelectorView>(R.id.selector_cliente)
        val selectorCarnet = findViewById<SelectorView>(R.id.selector_carnet)

        selectorCliente.setLabel("Filtrar por cliente:")
        val opcionesCliente = listOf("Todos", "Socio", "No Socio")
        selectorCliente.setOptions(opcionesCliente)

        selectorCarnet.setLabel("Filtrar por carnet:")
        val opcionesCarnet = listOf("Todos", "Con carnet", "Sin Carnet")
        selectorCarnet.setOptions(opcionesCarnet)

        // --- DATE PICKER ----- //
        val buttonDatePicker = findViewById<Button>(R.id.button_date_picker)
        buttonDatePicker.setOnClickListener {
            // 1. Crear el constructor del DatePicker para un RANGO de fechas
            val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Seleccione un rango de vencimiento")

            // 2. Construir el DatePicker
            val datePicker = datePickerBuilder.build()

            // 3. Mostrar el DatePicker
            datePicker.show(supportFragmentManager, "DATE_RANGE_PICKER_TAG")

            // 4. Escuchar cuando el usuario presiona "OK"
            datePicker.addOnPositiveButtonClickListener { selection ->
                // 'selection' es un Pair<Long, Long> con las fechas de inicio y fin en milisegundos
                val startDate = selection.first
                val endDate = selection.second

                // Formatear las fechas para mostrarlas en el botón
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDateString = sdf.format(Date(startDate))
                val endDateString = sdf.format(Date(endDate))

                // Actualizar el texto del botón con el rango seleccionado
                buttonDatePicker.text = "$startDateString - $endDateString"
            }


            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}