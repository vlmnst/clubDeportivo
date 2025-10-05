package com.example.clubdeportivo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
class ClientDetailDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.dialog_client_detail, container, false)
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = arguments?.getParcelable<Client>("client") ?: return

        // Referencias a las vistas del dialog
        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val tvNameComplete = view.findViewById<TextView>(R.id.tv_dialog_name)
        val tvDni = view.findViewById<TextView>(R.id.tv_dialog_dni)
        val tvVencimiento = view.findViewById<TextView>(R.id.tv_vencimiento_cuota)
        val tvInfoCarnet = view.findViewById<TextView>(R.id.tv_carnet_impreso_info)
        val btnCobrarActividad = view.findViewById<Button>(R.id.btn_cobrar_actividad)
        val layoutBotonesSocio = view.findViewById<LinearLayout>(R.id.layout_botones_socio)

        // Llenar datos comunes
        tvNameComplete.text = "Nombre y apellido: ${client.name} ${client.lastName}"
        tvDni.text = "DNI: ${client.dni}"

        // Lógica condicional: ¿Es socio?
        if (client.isPartner) {
            tvTitle.text = "Socio"
            tvVencimiento.visibility = View.VISIBLE
            tvInfoCarnet.visibility = View.VISIBLE
            layoutBotonesSocio.visibility = View.VISIBLE
            btnCobrarActividad.visibility = View.GONE

            // Aquí pondrías la fecha real
            tvVencimiento.text = "Vto de la cuota: 05/10/25"
        } else {
            tvTitle.text = "No Socio"
            // Las vistas de socio ya están en GONE por defecto en el XML,
            // pero es bueno ser explícito.
            tvVencimiento.visibility = View.GONE
            tvInfoCarnet.visibility = View.GONE
            layoutBotonesSocio.visibility = View.GONE
            btnCobrarActividad.visibility = View.VISIBLE
        }
    }

    companion object {
        // Función para crear una instancia del Dialog y pasarle datos de forma segura
        fun newInstance(client: Client): ClientDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("client", client)
            val fragment = ClientDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}