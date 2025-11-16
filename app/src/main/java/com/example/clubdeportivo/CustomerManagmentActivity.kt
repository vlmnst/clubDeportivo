package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class CustomerManagmentActivity : BaseActivity() {

    private var showCardEmpty = true
    private lateinit var clientAdapter: ClientAdapter
    private lateinit var dbHelper: BDatos
    private lateinit var allClients: List<Cliente>
    // Variables para guardar el estado actual de los filtros
    private var currentDniFilter: String = ""
    private var currentClientTypeFilter: String = "Todos" // Default: Todos
    private var currentCarnetFilter: String = "Todos"     // Default: Todos
    private var currentStartDateFilter: Long? = null
    private var currentEndDateFilter: Long? = null
    private var filterVtosToday: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)
        // BASE DE DATOS
        dbHelper = BDatos(this)
        allClients = dbHelper.obtenerClientes()

        // ----- INFO MOCK LISTADO -----//
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_clients)
        // ADAPTER PARA RENDERIZAR LOS CLIENTES
        clientAdapter = ClientAdapter(allClients) {
                client -> showDialogClient(client)
        }
        recyclerView.adapter = clientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // VARIABLES PARA LOS FILTROS
        val inputDniFilter = findViewById<EditText>(R.id.input_dni_filter)
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentDniFilter = s.toString().trim()
                applyFilters()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        inputDniFilter.addTextChangedListener(watcher)

        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        // --- SELECTORES ----- //
        val selectorClient = findViewById<SelectorView>(R.id.selector_cliente)
        val selectorCarnet = findViewById<SelectorView>(R.id.selector_carnet)

        selectorClient.setLabel("Filtrar por cliente:")
        val optionClient = listOf("Todos", "Socio", "No Socio")
        selectorClient.setOptions(optionClient)
        selectorClient.setOnOptionSelectedListener{ selectedOption ->
            currentClientTypeFilter = selectedOption
            applyFilters()
        }

        selectorCarnet.setLabel("Filtrar por carnet:")
        val optionCarnet = listOf("Todos", "Con carnet", "Sin Carnet")
        selectorCarnet.setOptions(optionCarnet)
        selectorCarnet.setOnOptionSelectedListener{ selectedOption ->
            currentCarnetFilter = selectedOption
            applyFilters()
        }

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

        // ------------------ CHECKBOX VENCIMIENTOS HOY ------------------ //
        val checkboxVencimientosDia = findViewById<com.google.android.material.checkbox.MaterialCheckBox>(R.id.checkbox_vencimientos_dia)

        checkboxVencimientosDia.setOnCheckedChangeListener { _, isChecked ->
            filterVtosToday = isChecked
            applyFilters() // Aplicar filtros al cambiar el estado del Checkbox
        }

            // --------- EMPTY AND FULL CLIENTS CARDS --------- //
            updateCardVisibility(allClients.isNotEmpty())


        // ----------------REDIRECCION DE LA CARD -------------//
        val linkRegistrarClient = findViewById<TextView>(R.id.text_registra_cliente)

        linkRegistrarClient?.setOnClickListener {
            val intent = Intent(this, RegistrarCliente::class.java)
            startActivity(intent)
        }
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

    // ------------------ FUNCIÓN CENTRAL (Filtro Maestro) ------------------ //

    private fun applyFilters() {
        var filteredList = allClients.toList() // Siempre empezamos con la lista COMPLETA

        // FILTRO DNI: Usamos la variable de estado actualizada
        if (currentDniFilter.isNotEmpty()) {
            filteredList = filteredList.filter { client ->
                client.dni.contains(currentDniFilter, ignoreCase = true)
            }
        }

        // FILTRO TIPO DE CLIENTE: Usamos la variable de estado actualizada
        filteredList = when (currentClientTypeFilter) {
            "Socio" -> filteredList.filter { it.socio }
            "No Socio" -> filteredList.filter { !it.socio }
            else -> filteredList
        }

        // FILTRO CARNET: Usamos la variable de estado actualizada
        filteredList = when (currentCarnetFilter) {
            "Con carnet" -> filteredList.filter { it.carnet }
            "Sin Carnet" -> filteredList.filter { !it.carnet }
            else -> filteredList
        }

        // Finalmente, actualizamos la vista con el resultado
        clientAdapter.updateDataClients(filteredList)
        updateCardVisibility(filteredList.isNotEmpty())
    }
    private fun updateCardVisibility(hasContent: Boolean) {
        val cardEmpty = findViewById<MaterialCardView>(R.id.card_empty_clients)
        val cardFull = findViewById<MaterialCardView>(R.id.card_full_clients)

        if (hasContent) {
            cardEmpty.visibility = View.GONE
            cardFull.visibility = View.VISIBLE
        } else {
            cardEmpty.visibility = View.VISIBLE
            cardFull.visibility = View.GONE
        }
    }
    private fun showDialogClient(client: Cliente) {
        val dialog = ClientDetailDialogFragment.newInstance(client)
        dialog.show(supportFragmentManager, "ClientDetailDialog")
    }


}