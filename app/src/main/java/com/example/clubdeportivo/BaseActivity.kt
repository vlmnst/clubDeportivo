package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


// Base para nav menu

// 'abstract' = es una plantilla y no se puede ejecutar por sí misma
abstract class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    fun setupNavigationDrawer() {
        // --- Config de la Toolbar ---
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // --- Conexión del Menú Hamburguesa ---

        // 1. Obtener el DrawerLayout (el contenedor)
        drawerLayout = findViewById(R.id.drawer_layout)

        // 2. Obtener TU ImageButton (el ícono de la hamburguesa)
        val menuButton = findViewById<ImageButton>(R.id.toolbar_menu_button)

        // 3. Asignar el clic para ABRIR el menú
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // --- Manejar clics DENTRO del menú ---
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            // Lógica para manejar la selección de ítems
            when (menuItem.itemId) {
                R.id.menu_principal -> {
                    // Si ya estamos en MenuPrincipal, solo cerramos el drawer
                    if (this !is MenuPrincipal) {
                        startActivity(Intent(this, MenuPrincipal::class.java))
                    }
                }
                R.id.nuevo_cliente -> {
                    if (this !is RegistrarCliente) { // Reemplaza con el nombre real de tu Activity
                        startActivity(Intent(this, RegistrarCliente::class.java))
                    }
                }
                R.id.customer_menu -> {
                    if (this !is CustomerManagmentActivity) {
                        startActivity(Intent(this, CustomerManagmentActivity::class.java))
                    }
                }
                R.id.nav_logout -> {
                    // Lógica para cerrar sesión
                    // Volvemos a MainActivity (Login) y cerramos las demás
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            // Cierra el menú después de un clic
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}