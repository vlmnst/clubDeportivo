package com.example.clubdeportivo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DatoAdapter(
    private val actividades: MutableList<Actividad>,
    private val onItemClick: (Actividad?) -> Unit
) : RecyclerView.Adapter<DatoAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItem: TextView = view.findViewById(R.id.tvItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dato, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvItem.text = "${actividad.nombre} - $${actividad.precio}"

        // --- EFECTO DE SELECCIÓN ---
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#B3E5FC")) // celeste claro
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        // --- CLICK ---
        holder.itemView.setOnClickListener {
            // Deseleccionar si se hace clic en la misma posición
            selectedPosition = if (selectedPosition == position) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
            onItemClick(if (selectedPosition == -1) null else actividad)
        }
    }

    override fun getItemCount(): Int = actividades.size

    fun agregarActividad(actividad: Actividad) {
        actividades.add(actividad)
        notifyItemInserted(actividades.size - 1)
    }
}
