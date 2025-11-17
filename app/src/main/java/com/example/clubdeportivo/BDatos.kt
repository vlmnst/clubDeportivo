package com.example.clubdeportivo


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// nombre de la DB
private val BD = "BaseDatosClub"
private val VERSION = 2
private const val TABLA_USUARIO = "Usuario"
private const val TABLA_CLIENTE = "Cliente"
private const val TABLA_SERVICIOS = "Servicios"


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
                "carnet INTEGER NOT NULL DEFAULT 0," +
                "fecha_pago_de_mes TEXT DEFAULT NULL" +
                ")"

        // Ejecutar la sentencia (tabla cliente=)
        db?.execSQL(crearTablaClienteSql)

        //Tabla Servicios
        val crearTablaServiciosSql = "CREATE TABLE IF NOT EXISTS $TABLA_SERVICIOS (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo_servicio TEXT NOT NULL, " +
                "monto TEXT NOT NULL" +
                ")"

        //Ejecutar la sentencia (tabla servicios)
        db?.execSQL(crearTablaServiciosSql)

        //insertar los servicios
        insertarServicios(db)
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

    //Crear los distintos servicios//

    private fun insertarServicios(db: SQLiteDatabase?) {
        val servicios = listOf(
            Pair ("Cuota Socio", "35000"),
            Pair ("Zumba", "3000"),
            Pair ("Funcional", "3500"),
            Pair ("Pilates", "4000"),
            Pair ("Musculación", "3500")
        )

        servicios.forEach { (tipo,monto) ->
            val valores = ContentValues().apply {
                put("tipo_servicio", tipo)
                put("monto", monto)
            }
            db?.insert(TABLA_SERVICIOS, null, valores)
        }
    }

    //obtener valor del monto//
    fun obtenerMontoServicio(tipoServicio: String): String {
        val db = readableDatabase
        var monto = ""

        val selection = "tipo_servicio = ?"
        val selectionArgs = arrayOf(tipoServicio)

        val cursor = db.query(
            TABLA_SERVICIOS,      // tabla
            arrayOf("monto"),     // columna a devolver
            selection,            // WHERE tipo_servicio = ?
            selectionArgs,        // argumentos del WHERE
            null,                 // groupBy
            null,                 // having
            null                  // orderBy
        )

        cursor.use {
            if (it.moveToFirst()) {
                monto = it.getString(it.getColumnIndexOrThrow("monto"))
            }
        }

        db.close()
        return monto
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

    fun agregarServicio(tipoServicio: String, monto: String) :Boolean{
        val db = this.writableDatabase

        return db.use { database ->
            val valores = ContentValues().apply {
                put("tipo_servicio", tipoServicio)
                put("monto", monto)
            }

            try{
                database.insertOrThrow(TABLA_SERVICIOS, null, valores)
                true
            } catch (e: Exception) {
                Log.e("BDatos", "Error al agregar servicio : ${e.message}")
                false
            }
        }
    }



    fun resetDatabase() {
        val db = writableDatabase

        db.execSQL("DROP TABLE IF EXISTS $TABLA_CLIENTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SERVICIOS")

        // Agregá todos los DROP que necesites

        onCreate(db)  // vuelve a crear las tablas

        db.close()
    }



}



