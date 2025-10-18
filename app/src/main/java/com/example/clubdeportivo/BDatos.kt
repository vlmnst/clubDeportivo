package com.example.clubdeportivo


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// nombre de la DB
private val BD = "BaseDatosClub"
private val VERSION = 1
private const val TABLA_USUARIO = "Usuario"


//  clase para insertarUsuarios
data class Usuario(val nombre: String, val clave: Int)

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

        // Ejecutar la sentencia
        db?.execSQL(crearTablaSql)

        // Insertar usuario "admin" y pass "1234"
        insertarAdmin(db)
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



    // Función para insertar un nuevo usuario
    fun insertarUsuario (usuario: Usuario): String {
        // Obtener una instancia de la base de datos para escribir
        val db = this.writableDatabase

        // el use cierra automaticamente la db
        db.use {
            val contenedor = ContentValues()
            contenedor.put("nombre", usuario.nombre)
            contenedor.put("clave", usuario.clave)

            // Insertar datos
            val resultado = it.insert("Usuario", null, contenedor)

            return if (resultado == -1.toLong()) {
                "Falla en la carga de datos"
            } else {
                "Insert exitoso"
            }
        }
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