package com.example.clubdeportivo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
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
import kotlin.math.ceil

class CustomerManagmentActivity : AppCompatActivity() {

    private var showCardEmpty = true
    private var currentPage = 0
    private var totalPages = 0
    private val limitForPage = 5
    private lateinit var listCompleteClients: List<Client>
    private lateinit var clientAdapter: ClientAdapter
    // Referencias a las vistas del paginador
    private lateinit var tvPageStatus: TextView
    private lateinit var btnFirst: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnLast: ImageButton
    private lateinit var paginationControls: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)

        // ----- ELEMENTOS DE PAGINACION ----- //
        paginationControls = findViewById(R.id.pagination_controls)
        tvPageStatus = findViewById(R.id.tv_page_status)
        btnFirst = findViewById(R.id.btn_first_page)
        btnPrev = findViewById(R.id.btn_prev_page)
        btnNext = findViewById(R.id.btn_next_page)
        btnLast = findViewById(R.id.btn_last_page)

        // ----- INFO MOCK LISTADO -----//

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_clients)
        listCompleteClients = listOf(
            Client("Juan", "Pérez", "39709589", true),
            Client("María", "García", "28123456", true),
            Client("Carlos", "López", "35789012", true),
            Client("Ana", "Martínez", "41234567", false),
            Client("Luis", "Rodríguez", "33456789", false),
            Client("Laura", "Sánchez", "40567890", true),
            Client("Pedro", "Gómez", "31234567", false),
            Client("Sofía", "Fernández", "42345678", false),
            Client("Miguel", "Torres", "38765432", true),
            Client("Elena", "Ramírez", "29876543", true),
            Client("David", "Jiménez", "36543210", true),
            Client("Isabel", "Ruiz", "43210987", false)
        )


        clientAdapter = ClientAdapter(emptyList()) {
            client -> showDialogClient(client)
        }
        recyclerView.adapter = clientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // --- SELECTORES ----- //
        val selectorClient = findViewById<SelectorView>(R.id.selector_cliente)
        val selectorCarnet = findViewById<SelectorView>(R.id.selector_carnet)

        selectorClient.setLabel("Filtrar por cliente:")
        val optionClient = listOf("Todos", "Socio", "No Socio")
        selectorClient.setOptions(optionClient)

        selectorCarnet.setLabel("Filtrar por carnet:")
        val optionCarnet = listOf("Todos", "Con carnet", "Sin Carnet")
        selectorCarnet.setOptions(optionCarnet)

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

            // --------- EMPTY AND FULL CLIENTS CARDS --------- //
            val cardEmpty = findViewById<MaterialCardView>(R.id.card_empty_clients)
            val cardFull = findViewById<MaterialCardView>(R.id.card_full_clients)
            val textViewClickable = findViewById<TextView>(R.id.clients_change)
            textViewClickable?.setOnClickListener {
                showCardEmpty = !showCardEmpty
                if (showCardEmpty) {
                    // Si se está mostrando la Card A, la ocultamos y mostramos la B
                    cardEmpty.visibility = View.VISIBLE
                    cardFull.visibility = View.GONE
                    paginationControls.visibility = View.GONE
                } else {
                    // Si se está mostrando la Card B, la ocultamos y mostramos la A
                    cardEmpty.visibility = View.GONE
                    cardFull.visibility = View.VISIBLE
                    updateViewPaginated()

                }
            }
        configPagination()
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


        // ----------- PAGINACIÓN ----------- //
        configPagination()
        updateViewPaginated()
        }

    private fun configPagination() {
        btnNext.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                updateViewPaginated()
            }
        }
        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updateViewPaginated()
            }
        }
        btnFirst.setOnClickListener {
            currentPage = 0
            updateViewPaginated()
        }
        btnLast.setOnClickListener {
            currentPage = totalPages - 1
            updateViewPaginated()
        }
    }
    private fun updateViewPaginated() {
        if(showCardEmpty) paginationControls.visibility = View.GONE
        else paginationControls.visibility = View.VISIBLE
        // 1. Calcular el número total de páginas
        totalPages = ceil(listCompleteClients.size.toDouble() / limitForPage).toInt()


        // 2. "Rebanar" la lista para obtener solo la página actual
        val inicio = currentPage * limitForPage
        val fin = minOf(inicio + limitForPage, listCompleteClients.size)
        val pageOfClients = listCompleteClients.subList(inicio, fin)

        // 3. Actualizar el adapter con los nuevos datos
        clientAdapter.updateDataClients(pageOfClients)

        // 4. Actualizar el texto del estado (ej: "1 de 2")
        tvPageStatus.text = "${currentPage + 1} de $totalPages"

        // 5. Habilitar o deshabilitar botones
        btnFirst.isEnabled = currentPage > 0
        btnPrev.isEnabled = currentPage > 0
        btnNext.isEnabled = currentPage < totalPages - 1
        btnLast.isEnabled = currentPage < totalPages - 1
    }

    private fun showDialogClient(client: Client) {
        val dialog = ClientDetailDialogFragment.newInstance(client)
        dialog.show(supportFragmentManager, "ClientDetailDialog")
    }


}