package com.example.clubdeportivo


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// nombre de la DB
private val BD = "BaseDatosClub"
private val VERSION = 1
private const val TABLA_USUARIO = "Usuario"
private const val TABLA_CLIENTE = "Cliente"

//Clase para CLiente
data class Cliente(
    val id: Int? = null, // El ID es nulo al insertar, pero se recibe al leer
    val nombre: String,
    val dni: String,
    val telefono: String?, // Nulable si no es obligatorio
    val email: String?,    // Nulable si no es obligatorio
    val apto: Boolean,
    val fecha_inscripcion: String, // Formato "AAAA-MM-DD"
    val socio: Boolean
)


// clase BDatos SQLiteOpenHelper
class BDatos(contexto: Context) : SQLiteOpenHelper(contexto, BD, null, VERSION) {

    //llama la primera vez que se accede a la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        //crear la tabla
        val crearTablaSql = "CREATE TABLE IF NOT EXISTS Usuario (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "clave TEXT NOT NULL" +
                ")"

        // Ejecutar la sentencia (tabla usuario)
        db?.execSQL(crearTablaSql)

        // Insertar usuario "admin" y pass "1234"
        insertarAdmin(db)

        // Tabla Cliente
        val crearTablaClienteSql = "CREATE TABLE IF NOT EXISTS $TABLA_CLIENTE (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "dni TEXT NOT NULL UNIQUE, " +
                "telefono TEXT, " +
                "email TEXT, " +
                "apto INTEGER DEFAULT 0, " + // booleanos INTEGER (0=false, 1=true)
                "fecha_inscripcion TEXT NOT NULL, " + // TEXTO - 2025-07-14 (año-mes-dia)
                "socio INTEGER NOT NULL DEFAULT 0" +  // 0=false, 1=true
                ")"

        // Ejecutar la sentencia (tabla cliente=)
        db?.execSQL(crearTablaClienteSql)
    }

    // obligatorio y para pruebas?
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // logica
        onCreate(db)
    }

    // Crea el usuario "admin" y pass "1234"
    private fun insertarAdmin(db: SQLiteDatabase?) {
        val valores = ContentValues()
        valores.put("nombre", "admin")
        valores.put("clave", "1234")

        db?.insert("Usuario", null, valores)
    }


    // AGREGAR UN NUEVO CLIENTE
    fun agregarCliente(cliente: Cliente): Boolean {
        val db = this.writableDatabase

        return db.use {
            val valores = ContentValues().apply {
                put("nombre", cliente.nombre)
                put("dni", cliente.dni)
                put("telefono", cliente.telefono)
                put("email", cliente.email)
                put("apto", if (cliente.apto) 1 else 0)
                put("fecha_inscripcion", cliente.fecha_inscripcion)
                put("socio", if (cliente.socio) 1 else 0)
            }

            try {
                // insertOrThrow devuelve un Long (ID)
                it.insertOrThrow(TABLA_CLIENTE, null, valores)
                true
            } catch (e: Exception) {
                // Si insertOrThrow lanza una excepción (ej. DNI duplicado),
                // se captura, se registra el error, y se devuelve 'false'.
                Log.e("BDatos", "Fallo al agregar cliente: ${e.message}")
                false
            }
        }
        }


    fun buscarClientePorDNI(dni: String): Cliente? {
        val db = this.readableDatabase
        var cliente: Cliente? = null

        val selection = "dni = ?"
        val selectionArgs = arrayOf(dni)

        val cursor = db.query(
            TABLA_CLIENTE,
            null, // null devuelve todas las columnas
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                cliente = Cliente(
                    id = it.getInt(it.getColumnIndexOrThrow("ID")),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                    dni = it.getString(it.getColumnIndexOrThrow("dni")),
                    telefono = it.getString(it.getColumnIndexOrThrow("telefono")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    apto = it.getInt(it.getColumnIndexOrThrow("apto")) == 1, // Convierte Integer a Boolean
                    fecha_inscripcion = it.getString(it.getColumnIndexOrThrow("fecha_inscripcion")),
                    socio = it.getInt(it.getColumnIndexOrThrow("socio")) == 1 // Convierte Integer a Boolean
                )
            }
        }
        db.close()
        return cliente
    }

    fun buscarClientePorDNIBool(dni: String): Boolean {
        val db = this.readableDatabase
        var existeCliente = false

        // Definir la consulta: queremos saber si hay alguna fila con ese DNI
        val selection = "dni = ?"
        val selectionArgs = arrayOf(dni)

        val cursor = db.query(
            TABLA_CLIENTE,
            arrayOf("ID"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                existeCliente = true
            }
        }

        db.close()

        return existeCliente
    }





    fun verificarUsuario(nombre: String, clave: String): Boolean {
        // Obtener una base de datos legible
        val db = this.readableDatabase
        var esValido = false

        // consulta para buscar el usuario
        val selection = "nombre = ? AND clave = ?"
        val selectionArgs = arrayOf(nombre, clave)

        // ejecutar la consulta
        val cursor = db.query(
            TABLA_USUARIO,      // La tabla a consultar
            arrayOf("ID"),      // Las columnas a devolver
            selection,          // La cláusula WHERE
            selectionArgs,      // Los valores para la cláusula WHERE
            null,               // groupBy
            null,               // having
            null                // orderBy
        )

        cursor.use {
            if (it.count > 0) {
                esValido = true
            }
        }

        // Cerrar la conexión a la base de datos
        db.close()
        return esValido
    }





}



