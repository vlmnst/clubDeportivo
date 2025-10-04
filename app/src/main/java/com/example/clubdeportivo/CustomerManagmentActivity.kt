package com.example.clubdeportivo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class CustomerManagmentActivity : AppCompatActivity() {

    private var showCardEmpty = true
    private val TAG = "CustomerManagment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)

        // ----- INFO MOCK LISTADO -----//

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_clients)
        val listaDeClientesMock = listOf(
            Client("Juan", "Pérez", "39709589"),
            Client("María", "García", "28123456"),
            Client("Carlos", "López", "35789012"),
            Client("Ana", "Martínez", "41234567"),
            Client("Luis", "Rodríguez", "33456789"),
            Client("Laura", "Sánchez", "40567890")
        )

        val clientAdapter = ClientAdapter(listaDeClientesMock)
        recyclerView.adapter = clientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


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
        }

            // --------- EMPTY AND FULL CLIENTS --------- //
            val cardEmpty = findViewById<MaterialCardView>(R.id.card_empty_clients)
            val cardFull = findViewById<MaterialCardView>(R.id.card_full_clients)
            val textViewClickable = findViewById<TextView>(R.id.clients_change)

            textViewClickable?.setOnClickListener {

                if (showCardEmpty) {
                    // Si se está mostrando la Card A, la ocultamos y mostramos la B
                    cardEmpty.visibility = View.GONE
                    cardFull.visibility = View.VISIBLE
                } else {
                    // Si se está mostrando la Card B, la ocultamos y mostramos la A
                    cardEmpty.visibility = View.VISIBLE
                    cardFull.visibility = View.GONE
                }

                //  Invertimos el estado de la variable para el próximo clic
                showCardEmpty = !showCardEmpty

                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                    insets
                }
            }
        }

}