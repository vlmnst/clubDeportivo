package com.example.clubdeportivo

import android.content.Intent
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

class CustomerManagmentActivity : BaseActivity() {

    private var showCardEmpty = true
    private lateinit var listCompleteClients: List<Cliente>
    private lateinit var clientAdapter: ClientAdapter
    val dbHelper = BDatos(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_managment)

        // ----- INFO MOCK LISTADO -----//
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_clients)
        listCompleteClients = dbHelper.obtenerClientes()


        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()
        clientAdapter = ClientAdapter(listCompleteClients) {
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
            if(listCompleteClients.isNotEmpty()) {
                showCardEmpty = !showCardEmpty
                if (showCardEmpty) {
                    // Si se está mostrando la Card A, la ocultamos y mostramos la B
                    cardEmpty.visibility = View.VISIBLE
                    cardFull.visibility = View.GONE
                } else {
                    // Si se está mostrando la Card B, la ocultamos y mostramos la A
                    cardEmpty.visibility = View.GONE
                    cardFull.visibility = View.VISIBLE

                }
            }

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

    private fun showDialogClient(client: Cliente) {
        val dialog = ClientDetailDialogFragment.newInstance(client)
        dialog.show(supportFragmentManager, "ClientDetailDialog")
    }


}