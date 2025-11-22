package com.example.clubdeportivo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ClientDetailDialogFragment : DialogFragment() {
    // 1. DECLARACIÓN DE VARIABLES DE CLASE
    private var currentClient: Cliente? = null
    private lateinit var dbHelper: BDatos
    private lateinit var tvTitle: TextView
    private lateinit var tvNameComplete: TextView
    private lateinit var tvDni: TextView
    private lateinit var tvVencimiento: TextView
    private lateinit var tvInfoCarnet: TextView
    private lateinit var btnCobrarActividad: Button
    private lateinit var layoutBotonesSocio: LinearLayout
    private lateinit var btnPrintID: Button
    private lateinit var btnCobrarCuota: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_client_detail, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentClient = arguments?.getParcelable<Cliente>("client") ?: return
        dbHelper = BDatos(requireContext()) // Inicializamos el DB Helper
        tvTitle = view.findViewById(R.id.tv_dialog_title)
        tvNameComplete = view.findViewById(R.id.tv_dialog_name)
        tvDni = view.findViewById(R.id.tv_dialog_dni)
        tvVencimiento = view.findViewById(R.id.tv_vencimiento_cuota)
        tvInfoCarnet = view.findViewById(R.id.tv_carnet_impreso_info)
        btnCobrarActividad = view.findViewById(R.id.btn_cobrar_actividad)
        layoutBotonesSocio = view.findViewById(R.id.layout_botones_socio)
        btnPrintID = view.findViewById(R.id.btn_imprimir_carnet)
        btnCobrarCuota = view.findViewById(R.id.btn_cobrar_cuota)

        cargarDatos()

        btnPrintID.setOnClickListener {
            val intent = Intent(requireContext(), Carnet::class.java).apply{
                putExtra("client_to_print", currentClient)
            }
            startActivity(intent)
        }
        btnCobrarCuota.setOnClickListener {
            val intent = Intent(requireContext(), CobroCuotaSocio::class.java)
            intent.putExtra("DNI", currentClient?.dni)

            startActivity(intent)
        }
        btnCobrarActividad.setOnClickListener {
            val intent = Intent(requireContext(), CobroActividadNoSocioActivity::class.java)
            intent.putExtra("DNI", currentClient?.dni)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarDatos() {
        val client = currentClient ?: return

        // Llenar datos comunes
        tvNameComplete.text = "Nombre y apellido: ${client.nombre}"
        tvDni.text = "DNI: ${client.dni}"

        // Lógica condicional: ¿Es socio?
        if (client.socio) {
            tvTitle.text = "Socio"
            tvVencimiento.visibility = View.VISIBLE
            tvInfoCarnet.visibility = View.VISIBLE

            // Para refrescar, si se imprimio el carnet aparecera.
            tvInfoCarnet.text = if(client.carnet) "¡Ya imprimiste el carnet de este socio!" else "Éste socio aún no tiene carnet"

            layoutBotonesSocio.visibility = View.VISIBLE
            btnCobrarActividad.visibility = View.GONE

            // Calculo fecha de vencimiento
            if(client.fechaPagoDeMes != null) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val fecha = LocalDate.parse(client.fechaPagoDeMes, formatter)
                val fechaVencimiento = fecha.plusMonths(1)
                tvVencimiento.text = "Vto de la cuota: ${fechaVencimiento}"
            }
        } else {
            tvTitle.text = "No Socio"
            tvVencimiento.visibility = View.GONE
            tvInfoCarnet.visibility = View.GONE
            layoutBotonesSocio.visibility = View.GONE
            btnCobrarActividad.visibility = View.VISIBLE
        }
    }

    //con este metodo recargamos los datos actualizaos de la BD
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val dniBusqueda = currentClient?.dni
        // Verificamos si podemos buscar datos
        if (dniBusqueda != null && dniBusqueda.isNotEmpty() && ::dbHelper.isInitialized) {
            // Leemos el cliente actualizado de la BD (con el campo 'carnet' modificado)
            val updatedClient = dbHelper.buscarClientePorDNI(dniBusqueda)

            if (updatedClient != null) {
                currentClient = updatedClient      // Actualizamos el objeto en memoria
                cargarDatos()                      // Refrescamos la UI con los nuevos datos
            }
        }
    }

    companion object {
        // Función para crear una instancia del Dialog y pasarle datos de forma segura
        fun newInstance(client: Cliente): ClientDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("client", client)
            val fragment = ClientDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}