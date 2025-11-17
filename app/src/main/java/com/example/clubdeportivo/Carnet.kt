package com.example.clubdeportivo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class Carnet : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)
        val client = intent.getParcelableExtra<Cliente>("client_to_print")
        val dbhelper = BDatos(this)
        // Esto ajusta los bordes del layout para pantallas modernas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //LLAMA A LA FUNCION DE LA BASEACTIVITY (nav menu)
        setupNavigationDrawer()

        // -----SETEAR LOS DATOS EN EL CARNET -----//
        val nameClient = findViewById<TextView>(R.id.txtNombre)
        nameClient?.text ="${client?.nombre}"
        val idClient = findViewById<TextView>(R.id.tv_socio)
        idClient?.text = "${"Socio Número : " + client?.id}"
        val dniClient = findViewById<TextView>(R.id.tv_dni)
        dniClient?.text = "${"DNI: "+ client?.dni}"
        val esApto = client?.apto ?: false
        val textoApto: String = if(esApto) "SI" else "NO"
        val aptoClient = findViewById<TextView>(R.id.tv_apto_fisico)
        aptoClient?.text = "Apto físico: $textoApto"
        val fechaInscipcionClient = findViewById<TextView>(R.id.tv_fecha_inscripcion)
        fechaInscipcionClient?.text = "${"Fecha Inscripción: " + client?.fecha_inscripcion}"

        //----- BOTONES FUNCIONES -------/
        val idImgCompartir = findViewById<ImageButton>(R.id.imgCompartir)
        val idImgDescargar = findViewById<ImageButton>(R.id.imgDescargar)

        idImgCompartir.setOnClickListener {
            val idSocio = client?.id
            if(idSocio != null) dbhelper.carnetImpreso(idSocio)
            val intent = Intent(this, Compartir::class.java)
            intent.putExtra("client_to_print", client)
            startActivity(intent)
        }

        idImgDescargar.setOnClickListener {
            val idSocio = client?.id
            if(idSocio != null) dbhelper.carnetImpreso(idSocio)
            Toast.makeText(this, "La descarga ha comenzado en segundo plano.", Toast.LENGTH_LONG).show()
        }
    }
}