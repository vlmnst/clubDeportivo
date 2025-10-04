package com.example.clubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClientAdapter(private val listaClientes: List<Client>) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Actualizamos las referencias a los nuevos IDs
        val nameTextView: TextView = itemView.findViewById(R.id.tv_name_client)
        val lastNameTextView: TextView = itemView.findViewById(R.id.tv_lastName_client)
        val dniTextView: TextView = itemView.findViewById(R.id.tv_dni_client)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val clienteActual = listaClientes[position]
        // Asignamos cada dato a su respectivo TextView
        holder.nameTextView.text = clienteActual.name
        holder.lastNameTextView.text = clienteActual.lastName
        holder.dniTextView.text = clienteActual.dni
    }

    override fun getItemCount(): Int {
        return listaClientes.size
    }
}
