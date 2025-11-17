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


// clase BDatos SQLiteOpenHelper
class BDatos(contexto: Context) : SQLiteOpenHelper(contexto, BD, null, VERSION) {

    //llama la primera vez que se accede a la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        //crear la tabla
        val crearTablaSql = "CREATE TABLE IF NOT EXISTS $TABLA_USUARIO (" +
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
                "socio INTEGER NOT NULL DEFAULT 0," +  // 0=false, 1=true
                "carnet INTEGER NOT NULL DEFAULT 0," + // 0=false, 1=true
                "fecha_pago_de_mes TEXT DEFAULT NULL" +
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
                put("carnet", if(cliente.carnet) 1 else 0)
                put("fecha_pago_de_mes", cliente.fechaPagoDeMes)
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
                    socio = it.getInt(it.getColumnIndexOrThrow("socio")) == 1, // Convierte Integer a Boolean
                    carnet = it.getInt(it.getColumnIndexOrThrow("carnet")) == 1,
                    fechaPagoDeMes = it.getString(it.getColumnIndexOrThrow("fecha_pago_de_mes"))
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


    fun obtenerClientes(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_CLIENTE", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inscripcion"))
                val apto = cursor.getInt(cursor.getColumnIndexOrThrow("apto")) == 1
                val socio = cursor.getInt(cursor.getColumnIndexOrThrow("socio")) == 1
                val carnet = cursor.getInt(cursor.getColumnIndexOrThrow("carnet")) == 1
                val fechaPagoDeMes = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago_de_mes"))

                val cliente = Cliente(id, nombre, dni, telefono, email, apto, fecha, socio, carnet, fechaPagoDeMes)
                lista.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun carnetImpreso(socioID : Int): Boolean{
        val db = this.writableDatabase

        val valores = ContentValues().apply {
            put("carnet", 1) //la columna carnet recibe 1 para true
        }
        val clausulaWhere = "ID=?"
        val argumentosWhere = arrayOf(socioID.toString())

        //actualizamos la bd

        return db.use {
            it.update(
                TABLA_CLIENTE,
                valores,
                clausulaWhere,
                argumentosWhere
            )
        } > 0
    }

    fun resetDatabase() {
        val db = writableDatabase

        db.execSQL("DROP TABLE IF EXISTS $TABLA_CLIENTE")
        // Agregá todos los DROP que necesites

        onCreate(db)  // vuelve a crear las tablas

        db.close()
    }



}



