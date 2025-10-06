package com.example.clubdeportivo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.clubdeportivo.R

class SelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val label: TextView
    private val spinner: Spinner

    init {
        // Inflamos el layout que hicimos (view_selector.xml)
        LayoutInflater.from(context).inflate(R.layout.view_selector, this, true)

        orientation = VERTICAL

        label = findViewById(R.id.selector_label)
        spinner = findViewById(R.id.selector_spinner)
    }

    // Función para setear el texto del label (ej: "País", "Provincia")
    fun setLabel(text: String) {
        label.text = text
    }

    // Función para setear la lista de opciones
    fun setOptions(options: List<String>) {
        val adapter = android.widget.ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    // Función para obtener el valor seleccionado
    fun getSelected(): String {
        return spinner.selectedItem?.toString() ?: ""
    }
}
