package com.duoc.silos.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.duoc.silos.data.model.Alerta
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.OrdenDespacho
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import com.duoc.silos.data.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SilosDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "silos_agrofeed360.db"
        const val DATABASE_VERSION = 4

        // Tabla Usuarios
        const val TABLE_USUARIOS = "usuarios"
        const val COL_USR_ID = "id"
        const val COL_USR_NOMBRE = "nombre"
        const val COL_USR_EMAIL = "email"
        const val COL_USR_CONTRASENA = "contrasena"
        const val COL_USR_ROL = "rol"
        const val COL_USR_CARGO = "cargo"
        const val COL_USR_ACTIVO = "activo"
        const val COL_USR_ULTIMO_ACCESO = "ultimo_acceso"

        // Tabla Silos
        const val TABLE_SILOS = "silos"
        const val COL_SILO_ID = "id"
        const val COL_SILO_FOTO_SECCION = "foto_seccion_uri"
        const val COL_SILO_GRANJA = "granja"
        const val COL_SILO_SECTOR = "sector_pabellon"
        const val COL_SILO_CAPACIDAD = "capacidad_total"
        const val COL_SILO_NIVEL = "nivel_actual"
        const val COL_SILO_TIPO_ALIMENTO = "tipo_alimento"
        const val COL_SILO_FECHA = "fecha_actualizacion"
        const val COL_SILO_ESTADO = "estado"
        const val COL_SILO_LATITUD = "latitud"
        const val COL_SILO_LONGITUD = "longitud"
        const val COL_SILO_CONSUMO_HORA = "consumo_hora"
        const val COL_SILO_TEMP = "temperatura"
        const val COL_SILO_HUMEDAD = "humedad"

        // Tabla Movimientos
        const val TABLE_MOVIMIENTOS = "movimientos"
        const val COL_MOV_ID = "id"
        const val COL_MOV_SILO_ID = "silo_id"
        const val COL_MOV_GRANJA = "granja"
        const val COL_MOV_TIPO = "tipo"
        const val COL_MOV_CANTIDAD = "cantidad"
        const val COL_MOV_FECHA = "fecha_hora"
        const val COL_MOV_OPERARIO = "operario"
        const val COL_MOV_OBS = "observaciones"
        const val COL_MOV_LAT = "latitud"
        const val COL_MOV_LON = "longitud"
        const val COL_MOV_FOTO = "foto_uri"
        const val COL_MOV_PATENTE = "patente"
        const val COL_MOV_GUIA = "numero_guia"

        // Tabla Alertas
        const val TABLE_ALERTAS = "alertas"
        const val COL_ALE_ID = "id"
        const val COL_ALE_SILO_ID = "silo_id"
        const val COL_ALE_GRANJA = "granja"
        const val COL_ALE_MENSAJE = "mensaje"
        const val COL_ALE_ESTADO = "estado"
        const val COL_ALE_FECHA = "fecha_hora"
        const val COL_ALE_ATENDIDA = "atendida"
        const val COL_ALE_HORAS_AGOTAMIENTO = "horas_agotamiento"

        // Tabla Órdenes Despacho Tolva
        const val TABLE_ORDENES = "ordenes_despacho"
        const val COL_ORD_ID = "id"
        const val COL_ORD_SILO_ID = "silo_id"
        const val COL_ORD_GRANJA = "granja"
        const val COL_ORD_CANTIDAD = "cantidad_kg"
        const val COL_ORD_ALIMENTO = "tipo_alimento"
        const val COL_ORD_FECHA = "fecha_hora"
        const val COL_ORD_CHOFER = "conductor"
        const val COL_ORD_PATENTE = "patente"
        const val COL_ORD_ETA = "eta"
        const val COL_ORD_ESTADO = "estado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSilos = """
            CREATE TABLE $TABLE_SILOS (
                $COL_SILO_ID TEXT PRIMARY KEY,
                $COL_SILO_FOTO_SECCION TEXT,
                $COL_SILO_GRANJA TEXT NOT NULL,
                $COL_SILO_SECTOR TEXT NOT NULL,
                $COL_SILO_CAPACIDAD REAL NOT NULL,
                $COL_SILO_NIVEL REAL NOT NULL,
                $COL_SILO_TIPO_ALIMENTO TEXT NOT NULL,
                $COL_SILO_FECHA TEXT NOT NULL,
                $COL_SILO_ESTADO TEXT NOT NULL,
                $COL_SILO_LATITUD REAL NOT NULL,
                $COL_SILO_LONGITUD REAL NOT NULL,
                $COL_SILO_CONSUMO_HORA REAL NOT NULL,
                $COL_SILO_TEMP REAL NOT NULL,
                $COL_SILO_HUMEDAD REAL NOT NULL
            )
        """.trimIndent()

        val createMovimientos = """
            CREATE TABLE $TABLE_MOVIMIENTOS (
                $COL_MOV_ID TEXT PRIMARY KEY,
                $COL_MOV_SILO_ID TEXT NOT NULL,
                $COL_MOV_GRANJA TEXT NOT NULL,
                $COL_MOV_TIPO TEXT NOT NULL,
                $COL_MOV_CANTIDAD REAL NOT NULL,
                $COL_MOV_FECHA TEXT NOT NULL,
                $COL_MOV_OPERARIO TEXT NOT NULL,
                $COL_MOV_OBS TEXT,
                $COL_MOV_LAT REAL NOT NULL,
                $COL_MOV_LON REAL NOT NULL,
                $COL_MOV_FOTO TEXT,
                $COL_MOV_PATENTE TEXT,
                $COL_MOV_GUIA TEXT
            )
        """.trimIndent()

        val createAlertas = """
            CREATE TABLE $TABLE_ALERTAS (
                $COL_ALE_ID TEXT PRIMARY KEY,
                $COL_ALE_SILO_ID TEXT NOT NULL,
                $COL_ALE_GRANJA TEXT NOT NULL,
                $COL_ALE_MENSAJE TEXT NOT NULL,
                $COL_ALE_ESTADO TEXT NOT NULL,
                $COL_ALE_FECHA TEXT NOT NULL,
                $COL_ALE_ATENDIDA INTEGER NOT NULL DEFAULT 0,
                $COL_ALE_HORAS_AGOTAMIENTO INTEGER NOT NULL DEFAULT 12
            )
        """.trimIndent()

        val createOrdenes = """
            CREATE TABLE $TABLE_ORDENES (
                $COL_ORD_ID TEXT PRIMARY KEY,
                $COL_ORD_SILO_ID TEXT NOT NULL,
                $COL_ORD_GRANJA TEXT NOT NULL,
                $COL_ORD_CANTIDAD REAL NOT NULL,
                $COL_ORD_ALIMENTO TEXT NOT NULL,
                $COL_ORD_FECHA TEXT NOT NULL,
                $COL_ORD_CHOFER TEXT NOT NULL,
                $COL_ORD_PATENTE TEXT NOT NULL,
                $COL_ORD_ETA TEXT NOT NULL,
                $COL_ORD_ESTADO TEXT NOT NULL
            )
        """.trimIndent()

        val createUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_USR_ID TEXT PRIMARY KEY,
                $COL_USR_NOMBRE TEXT NOT NULL,
                $COL_USR_EMAIL TEXT UNIQUE NOT NULL,
                $COL_USR_CONTRASENA TEXT NOT NULL,
                $COL_USR_ROL TEXT NOT NULL,
                $COL_USR_CARGO TEXT NOT NULL,
                $COL_USR_ACTIVO INTEGER NOT NULL DEFAULT 1,
                $COL_USR_ULTIMO_ACCESO TEXT
            )
        """.trimIndent()

        db.execSQL(createSilos)
        db.execSQL(createMovimientos)
        db.execSQL(createAlertas)
        db.execSQL(createOrdenes)
        db.execSQL(createUsuarios)

        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDENES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALERTAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIMIENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SILOS")
        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        val silos = listOf(
            Silo("SILO-01", "placeholder", "Plantel Melipilla Central", "Pabellón Broilers 1 y 2", 22000.0, 18500.0, "Iniciador Aves Premium", "26-10-2026 15:40", EstadoSilo.NORMAL, -33.6882, -71.2185, 125.0, 21.8, 54.0),
            Silo("SILO-02", "placeholder", "Plantel Melipilla Central", "Pabellón Broilers 3 y 4", 20000.0, 4200.0, "Engorde Aves Pellet", "26-10-2026 15:35", EstadoSilo.ADVERTENCIA, -33.6895, -71.2198, 140.0, 23.1, 56.5),
            Silo("SILO-03", "placeholder", "Plantel Mallarauco Aves", "Pabellón Pavos Reproductoras", 28000.0, 2600.0, "Crecimiento Pavo Extruido", "26-10-2026 15:30", EstadoSilo.CRITICO, -33.6120, -71.1890, 180.0, 24.2, 59.0),
            Silo("SILO-04", "placeholder", "Plantel Mallarauco Aves", "Pabellón Ponedoras Huevos", 16000.0, 14800.0, "Postura Alta Energía", "26-10-2026 12:15", EstadoSilo.NORMAL, -33.6135, -71.1882, 95.0, 20.9, 52.0),
            Silo("SILO-05", "placeholder", "Fundo San Pedro Ganadero", "Establo Lechero Vacuno A", 32000.0, 4800.0, "Ración Total Mezclada (TMR)", "26-10-2026 14:10", EstadoSilo.CRITICO, -33.8920, -71.4530, 250.0, 22.0, 58.0),
            Silo("SILO-06", "placeholder", "Fundo San Pedro Ganadero", "Engorde Bovino Lote Norte", 25000.0, 19200.0, "Concentrado Granulado Bovino", "26-10-2026 11:45", EstadoSilo.NORMAL, -33.8945, -71.4510, 160.0, 21.5, 55.0)
        )

        for (s in silos) {
            val cv = ContentValues().apply {
                put(COL_SILO_ID, s.id)
                put(COL_SILO_FOTO_SECCION, s.fotoSeccionUri)
                put(COL_SILO_GRANJA, s.granja)
                put(COL_SILO_SECTOR, s.sectorPabellon)
                put(COL_SILO_CAPACIDAD, s.capacidadTotalKg)
                put(COL_SILO_NIVEL, s.nivelActualKg)
                put(COL_SILO_TIPO_ALIMENTO, s.tipoAlimento)
                put(COL_SILO_FECHA, s.fechaActualizacion)
                put(COL_SILO_ESTADO, s.estado.name)
                put(COL_SILO_LATITUD, s.latitud)
                put(COL_SILO_LONGITUD, s.longitud)
                put(COL_SILO_CONSUMO_HORA, s.consumoPromedioKgHora)
                put(COL_SILO_TEMP, s.temperaturaInternaC)
                put(COL_SILO_HUMEDAD, s.humedadRelativa)
            }
            db.insert(TABLE_SILOS, null, cv)
        }

        val movimientos = listOf(
            Movimiento("MOV-9001", "SILO-03", "Plantel Mallarauco Aves", TipoMovimiento.CONSUMO, 4200.0, "26-10-2026 09:30", "Matías Fuentes", "Alimentación matutina tolva dosificadora pabellón 2", -33.6120, -71.1890, null, "", ""),
            Movimiento("MOV-9002", "SILO-01", "Plantel Melipilla Central", TipoMovimiento.CARGA, 8500.0, "26-10-2026 11:15", "Rodrigo Díaz", "Descarga camión tolva hermético", -33.6882, -71.2185, null, "KJ-9942", "GD-88412"),
            Movimiento("MOV-9003", "SILO-02", "Plantel Melipilla Central", TipoMovimiento.CONSUMO, 3100.0, "26-10-2026 13:40", "Carlos Muñoz", "Ración controlada horario mediodía", -33.6895, -71.2198, null, "", ""),
            Movimiento("MOV-9004", "SILO-05", "Fundo San Pedro Ganadero", TipoMovimiento.CONSUMO, 5500.0, "26-10-2026 14:00", "Patricio Soto", "Descarga a mixer lechero", -33.8920, -71.4530, null, "", "")
        )

        for (m in movimientos) {
            val cv = ContentValues().apply {
                put(COL_MOV_ID, m.id)
                put(COL_MOV_SILO_ID, m.siloId)
                put(COL_MOV_GRANJA, m.granja)
                put(COL_MOV_TIPO, m.tipo.name)
                put(COL_MOV_CANTIDAD, m.cantidadKg)
                put(COL_MOV_FECHA, m.fechaHora)
                put(COL_MOV_OPERARIO, m.operario)
                put(COL_MOV_OBS, m.observaciones)
                put(COL_MOV_LAT, m.latitud)
                put(COL_MOV_LON, m.longitud)
                put(COL_MOV_FOTO, m.evidenciaFotoUri)
                put(COL_MOV_PATENTE, m.patenteCamion)
                put(COL_MOV_GUIA, m.numeroGuia)
            }
            db.insert(TABLE_MOVIMIENTOS, null, cv)
        }

        val alertas = listOf(
            Alerta("ALT-01", "SILO-03", "Plantel Mallarauco Aves", "Nivel crítico: 9.2% (2.600 kg). Riesgo de desabastecimiento en 14 hrs. Despachar tolva urgente.", EstadoSilo.CRITICO, "26-10-2026 15:30", false, 14),
            Alerta("ALT-02", "SILO-05", "Fundo San Pedro Ganadero", "Nivel crítico: 15.0% (4.800 kg). Ración lechera en umbral de seguridad. Autonomía: 19 hrs.", EstadoSilo.CRITICO, "26-10-2026 14:10", false, 19),
            Alerta("ALT-03", "SILO-02", "Plantel Melipilla Central", "Advertencia de stock: 21.0% (4.200 kg). Programar despacho para turno noche.", EstadoSilo.ADVERTENCIA, "26-10-2026 15:35", false, 30)
        )

        for (a in alertas) {
            val cv = ContentValues().apply {
                put(COL_ALE_ID, a.id)
                put(COL_ALE_SILO_ID, a.siloId)
                put(COL_ALE_GRANJA, a.granja)
                put(COL_ALE_MENSAJE, a.mensaje)
                put(COL_ALE_ESTADO, a.estado.name)
                put(COL_ALE_FECHA, a.fechaHora)
                put(COL_ALE_ATENDIDA, if (a.atendida) 1 else 0)
                put(COL_ALE_HORAS_AGOTAMIENTO, a.horasEstimadasAgotamiento)
            }
            db.insert(TABLE_ALERTAS, null, cv)
        }

        val ordenes = listOf(
            OrdenDespacho("DSP-4011", "SILO-03", "Plantel Mallarauco Aves", 22000.0, "Crecimiento Pavo Extruido", "26-10-2026 15:32", "Jorge Alarcón", "TL-8821", "40 minutos (En Ruta G-60)")
        )

        for (o in ordenes) {
            val cv = ContentValues().apply {
                put(COL_ORD_ID, o.id)
                put(COL_ORD_SILO_ID, o.siloId)
                put(COL_ORD_GRANJA, o.granja)
                put(COL_ORD_CANTIDAD, o.cantidadSolicitadaKg)
                put(COL_ORD_ALIMENTO, o.tipoAlimento)
                put(COL_ORD_FECHA, o.fechaHoraSolicitud)
                put(COL_ORD_CHOFER, o.conductor)
                put(COL_ORD_PATENTE, o.patenteTolva)
                put(COL_ORD_ETA, o.tiempoEstimadoLlegada)
                put(COL_ORD_ESTADO, o.estado)
            }
            db.insert(TABLE_ORDENES, null, cv)
        }

        val usuarios = listOf(
            Usuario("USR-001", "Rodrigo Díaz", "operario@silos.cl", "operario123", RolUsuario.OPERARIO, "Operador de Terreno y Despacho", true, "26-10-2026 15:00"),
            Usuario("USR-002", "Carolina Morales", "supervisor@silos.cl", "supervisor123", RolUsuario.SUPERVISOR, "Supervisora de Planta & Telemetría", true, "26-10-2026 15:30"),
            Usuario("USR-003", "Gonzalo Valenzuela", "jefatura@silos.cl", "jefatura123", RolUsuario.JEFATURA, "Jefe de Logística & Abastecimiento", true, "26-10-2026 14:00")
        )

        for (u in usuarios) {
            val cv = ContentValues().apply {
                put(COL_USR_ID, u.id)
                put(COL_USR_NOMBRE, u.nombre)
                put(COL_USR_EMAIL, u.email.lowercase(Locale.ROOT))
                put(COL_USR_CONTRASENA, u.contrasena)
                put(COL_USR_ROL, u.rol.name)
                put(COL_USR_CARGO, u.cargo)
                put(COL_USR_ACTIVO, if (u.activo) 1 else 0)
                put(COL_USR_ULTIMO_ACCESO, u.ultimoAcceso)
            }
            db.insert(TABLE_USUARIOS, null, cv)
        }
    }

    fun obtenerSilos(): List<Silo> {
        val lista = mutableListOf<Silo>()
        val db = readableDatabase
        val cursor = db.query(TABLE_SILOS, null, null, null, null, null, "$COL_SILO_ESTADO DESC, $COL_SILO_ID ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Silo(
                        id = c.getString(c.getColumnIndexOrThrow(COL_SILO_ID)),
                        fotoSeccionUri = c.getString(c.getColumnIndexOrThrow(COL_SILO_FOTO_SECCION)),
                        granja = c.getString(c.getColumnIndexOrThrow(COL_SILO_GRANJA)),
                        sectorPabellon = c.getString(c.getColumnIndexOrThrow(COL_SILO_SECTOR)),
                        capacidadTotalKg = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_CAPACIDAD)),
                        nivelActualKg = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_NIVEL)),
                        tipoAlimento = c.getString(c.getColumnIndexOrThrow(COL_SILO_TIPO_ALIMENTO)),
                        fechaActualizacion = c.getString(c.getColumnIndexOrThrow(COL_SILO_FECHA)),
                        estado = EstadoSilo.valueOf(c.getString(c.getColumnIndexOrThrow(COL_SILO_ESTADO))),
                        latitud = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_LATITUD)),
                        longitud = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_LONGITUD)),
                        consumoPromedioKgHora = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_CONSUMO_HORA)),
                        temperaturaInternaC = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_TEMP)),
                        humedadRelativa = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_HUMEDAD))
                    )
                )
            }
        }
        return lista
    }

    fun crearSilo(silo: Silo): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_SILO_ID, silo.id)
            put(COL_SILO_FOTO_SECCION, silo.fotoSeccionUri)
            put(COL_SILO_GRANJA, silo.granja)
            put(COL_SILO_SECTOR, silo.sectorPabellon)
            put(COL_SILO_CAPACIDAD, silo.capacidadTotalKg)
            put(COL_SILO_NIVEL, silo.nivelActualKg)
            put(COL_SILO_TIPO_ALIMENTO, silo.tipoAlimento)
            put(COL_SILO_FECHA, silo.fechaActualizacion)
            put(COL_SILO_ESTADO, silo.estado.name)
            put(COL_SILO_LATITUD, silo.latitud)
            put(COL_SILO_LONGITUD, silo.longitud)
            put(COL_SILO_CONSUMO_HORA, silo.consumoPromedioKgHora)
            put(COL_SILO_TEMP, silo.temperaturaInternaC)
            put(COL_SILO_HUMEDAD, silo.humedadRelativa)
        }
        val resultado = db.insertWithOnConflict(TABLE_SILOS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return resultado != -1L
    }

    fun obtenerSiloPorId(id: String): Silo? {
        val db = readableDatabase
        val cursor = db.query(TABLE_SILOS, null, "$COL_SILO_ID = ?", arrayOf(id), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return Silo(
                    id = c.getString(c.getColumnIndexOrThrow(COL_SILO_ID)),
                    fotoSeccionUri = c.getString(c.getColumnIndexOrThrow(COL_SILO_FOTO_SECCION)),
                    granja = c.getString(c.getColumnIndexOrThrow(COL_SILO_GRANJA)),
                    sectorPabellon = c.getString(c.getColumnIndexOrThrow(COL_SILO_SECTOR)),
                    capacidadTotalKg = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_CAPACIDAD)),
                    nivelActualKg = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_NIVEL)),
                    tipoAlimento = c.getString(c.getColumnIndexOrThrow(COL_SILO_TIPO_ALIMENTO)),
                    fechaActualizacion = c.getString(c.getColumnIndexOrThrow(COL_SILO_FECHA)),
                    estado = EstadoSilo.valueOf(c.getString(c.getColumnIndexOrThrow(COL_SILO_ESTADO))),
                    latitud = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_LATITUD)),
                    longitud = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_LONGITUD)),
                    consumoPromedioKgHora = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_CONSUMO_HORA)),
                    temperaturaInternaC = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_TEMP)),
                    humedadRelativa = c.getDouble(c.getColumnIndexOrThrow(COL_SILO_HUMEDAD))
                )
            }
        }
        return null
    }

    fun registrarMovimiento(movimiento: Movimiento): Silo? {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val siloActual = obtenerSiloPorId(movimiento.siloId) ?: return null

            var nuevoNivel = when (movimiento.tipo) {
                TipoMovimiento.CARGA -> siloActual.nivelActualKg + movimiento.cantidadKg
                TipoMovimiento.DESCARGA, TipoMovimiento.CONSUMO -> siloActual.nivelActualKg - movimiento.cantidadKg
            }
            if (nuevoNivel < 0) nuevoNivel = 0.0
            if (nuevoNivel > siloActual.capacidadTotalKg) nuevoNivel = siloActual.capacidadTotalKg

            val porcentaje = if (siloActual.capacidadTotalKg > 0) (nuevoNivel / siloActual.capacidadTotalKg) else 0.0
            val nuevoEstado = when {
                porcentaje <= 0.20 -> EstadoSilo.CRITICO
                porcentaje <= 0.35 -> EstadoSilo.ADVERTENCIA
                else -> EstadoSilo.NORMAL
            }

            val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

            val cvMov = ContentValues().apply {
                put(COL_MOV_ID, movimiento.id)
                put(COL_MOV_SILO_ID, movimiento.siloId)
                put(COL_MOV_GRANJA, movimiento.granja)
                put(COL_MOV_TIPO, movimiento.tipo.name)
                put(COL_MOV_CANTIDAD, movimiento.cantidadKg)
                put(COL_MOV_FECHA, ahora)
                put(COL_MOV_OPERARIO, movimiento.operario)
                put(COL_MOV_OBS, movimiento.observaciones)
                put(COL_MOV_LAT, movimiento.latitud)
                put(COL_MOV_LON, movimiento.longitud)
                put(COL_MOV_FOTO, movimiento.evidenciaFotoUri)
                put(COL_MOV_PATENTE, movimiento.patenteCamion)
                put(COL_MOV_GUIA, movimiento.numeroGuia)
            }
            db.insert(TABLE_MOVIMIENTOS, null, cvMov)

            val cvSilo = ContentValues().apply {
                put(COL_SILO_NIVEL, nuevoNivel)
                put(COL_SILO_FECHA, ahora)
                put(COL_SILO_ESTADO, nuevoEstado.name)
            }
            db.update(TABLE_SILOS, cvSilo, "$COL_SILO_ID = ?", arrayOf(movimiento.siloId))

            if (nuevoEstado == EstadoSilo.CRITICO || nuevoEstado == EstadoSilo.ADVERTENCIA) {
                val horasRestantes = if (siloActual.consumoPromedioKgHora > 0) (nuevoNivel / siloActual.consumoPromedioKgHora).toInt() else 10
                val mensajeAlerta = if (nuevoEstado == EstadoSilo.CRITICO) {
                    "ALERTA CRÍTICA: ${(porcentaje * 100).toInt()}% restante tras ${movimiento.tipo.name.lowercase()}. Agotamiento en ~$horasRestantes hrs."
                } else {
                    "ADVERTENCIA DE STOCK: ${(porcentaje * 100).toInt()}% restante tras ${movimiento.tipo.name.lowercase()}."
                }
                val alertaId = "ALT-${System.currentTimeMillis() % 10000}"
                val cvAle = ContentValues().apply {
                    put(COL_ALE_ID, alertaId)
                    put(COL_ALE_SILO_ID, movimiento.siloId)
                    put(COL_ALE_GRANJA, movimiento.granja)
                    put(COL_ALE_MENSAJE, mensajeAlerta)
                    put(COL_ALE_ESTADO, nuevoEstado.name)
                    put(COL_ALE_FECHA, ahora)
                    put(COL_ALE_ATENDIDA, 0)
                    put(COL_ALE_HORAS_AGOTAMIENTO, horasRestantes)
                }
                db.insert(TABLE_ALERTAS, null, cvAle)
            }

            db.setTransactionSuccessful()
            return siloActual.copy(
                nivelActualKg = nuevoNivel,
                fechaActualizacion = ahora,
                estado = nuevoEstado
            )
        } finally {
            db.endTransaction()
        }
    }

    fun crearOrdenDespacho(orden: OrdenDespacho) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ORD_ID, orden.id)
            put(COL_ORD_SILO_ID, orden.siloId)
            put(COL_ORD_GRANJA, orden.granja)
            put(COL_ORD_CANTIDAD, orden.cantidadSolicitadaKg)
            put(COL_ORD_ALIMENTO, orden.tipoAlimento)
            put(COL_ORD_FECHA, orden.fechaHoraSolicitud)
            put(COL_ORD_CHOFER, orden.conductor)
            put(COL_ORD_PATENTE, orden.patenteTolva)
            put(COL_ORD_ETA, orden.tiempoEstimadoLlegada)
            put(COL_ORD_ESTADO, orden.estado)
        }
        db.insert(TABLE_ORDENES, null, cv)
    }

    fun obtenerOrdenes(): List<OrdenDespacho> {
        val lista = mutableListOf<OrdenDespacho>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ORDENES, null, null, null, null, null, "$COL_ORD_FECHA DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                lista.add(
                    OrdenDespacho(
                        id = c.getString(c.getColumnIndexOrThrow(COL_ORD_ID)),
                        siloId = c.getString(c.getColumnIndexOrThrow(COL_ORD_SILO_ID)),
                        granja = c.getString(c.getColumnIndexOrThrow(COL_ORD_GRANJA)),
                        cantidadSolicitadaKg = c.getDouble(c.getColumnIndexOrThrow(COL_ORD_CANTIDAD)),
                        tipoAlimento = c.getString(c.getColumnIndexOrThrow(COL_ORD_ALIMENTO)),
                        fechaHoraSolicitud = c.getString(c.getColumnIndexOrThrow(COL_ORD_FECHA)),
                        conductor = c.getString(c.getColumnIndexOrThrow(COL_ORD_CHOFER)),
                        patenteTolva = c.getString(c.getColumnIndexOrThrow(COL_ORD_PATENTE)),
                        tiempoEstimadoLlegada = c.getString(c.getColumnIndexOrThrow(COL_ORD_ETA)),
                        estado = c.getString(c.getColumnIndexOrThrow(COL_ORD_ESTADO))
                    )
                )
            }
        }
        return lista
    }

    fun obtenerMovimientos(): List<Movimiento> {
        val lista = mutableListOf<Movimiento>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MOVIMIENTOS, null, null, null, null, null, "$COL_MOV_FECHA DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Movimiento(
                        id = c.getString(c.getColumnIndexOrThrow(COL_MOV_ID)),
                        siloId = c.getString(c.getColumnIndexOrThrow(COL_MOV_SILO_ID)),
                        granja = c.getString(c.getColumnIndexOrThrow(COL_MOV_GRANJA)),
                        tipo = TipoMovimiento.valueOf(c.getString(c.getColumnIndexOrThrow(COL_MOV_TIPO))),
                        cantidadKg = c.getDouble(c.getColumnIndexOrThrow(COL_MOV_CANTIDAD)),
                        fechaHora = c.getString(c.getColumnIndexOrThrow(COL_MOV_FECHA)),
                        operario = c.getString(c.getColumnIndexOrThrow(COL_MOV_OPERARIO)),
                        observaciones = c.getString(c.getColumnIndexOrThrow(COL_MOV_OBS)) ?: "",
                        latitud = c.getDouble(c.getColumnIndexOrThrow(COL_MOV_LAT)),
                        longitud = c.getDouble(c.getColumnIndexOrThrow(COL_MOV_LON)),
                        evidenciaFotoUri = c.getString(c.getColumnIndexOrThrow(COL_MOV_FOTO)),
                        patenteCamion = c.getString(c.getColumnIndexOrThrow(COL_MOV_PATENTE)) ?: "",
                        numeroGuia = c.getString(c.getColumnIndexOrThrow(COL_MOV_GUIA)) ?: ""
                    )
                )
            }
        }
        return lista
    }

    fun obtenerAlertas(): List<Alerta> {
        val lista = mutableListOf<Alerta>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ALERTAS, null, null, null, null, null, "$COL_ALE_FECHA DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Alerta(
                        id = c.getString(c.getColumnIndexOrThrow(COL_ALE_ID)),
                        siloId = c.getString(c.getColumnIndexOrThrow(COL_ALE_SILO_ID)),
                        granja = c.getString(c.getColumnIndexOrThrow(COL_ALE_GRANJA)),
                        mensaje = c.getString(c.getColumnIndexOrThrow(COL_ALE_MENSAJE)),
                        estado = EstadoSilo.valueOf(c.getString(c.getColumnIndexOrThrow(COL_ALE_ESTADO))),
                        fechaHora = c.getString(c.getColumnIndexOrThrow(COL_ALE_FECHA)),
                        atendida = c.getInt(c.getColumnIndexOrThrow(COL_ALE_ATENDIDA)) == 1,
                        horasEstimadasAgotamiento = c.getInt(c.getColumnIndexOrThrow(COL_ALE_HORAS_AGOTAMIENTO))
                    )
                )
            }
        }
        return lista
    }

    fun marcarAlertaAtendida(alertaId: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ALE_ATENDIDA, 1)
        }
        db.update(TABLE_ALERTAS, cv, "$COL_ALE_ID = ?", arrayOf(alertaId))
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE USUARIOS Y AUTENTICACIÓN
    // ==========================================

    fun autenticarUsuario(email: String, contrasena: String): Usuario? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "LOWER($COL_USR_EMAIL) = LOWER(?) AND $COL_USR_CONTRASENA = ? AND $COL_USR_ACTIVO = 1",
            arrayOf(email.trim(), contrasena),
            null, null, null
        )
        cursor.use { c ->
            if (c.moveToFirst()) {
                val usuario = cursorToUsuario(c)
                actualizarUltimoAcceso(usuario.id)
                return usuario
            }
        }
        return null
    }

    fun registrarUsuario(usuario: Usuario): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_USR_ID, usuario.id)
            put(COL_USR_NOMBRE, usuario.nombre)
            put(COL_USR_EMAIL, usuario.email.trim().lowercase(Locale.ROOT))
            put(COL_USR_CONTRASENA, usuario.contrasena)
            put(COL_USR_ROL, usuario.rol.name)
            put(COL_USR_CARGO, usuario.cargo)
            put(COL_USR_ACTIVO, if (usuario.activo) 1 else 0)
            put(COL_USR_ULTIMO_ACCESO, usuario.ultimoAcceso)
        }
        val resultado = db.insert(TABLE_USUARIOS, null, cv)
        return resultado != -1L
    }

    fun existeEmail(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            arrayOf(COL_USR_ID),
            "LOWER($COL_USR_EMAIL) = LOWER(?)",
            arrayOf(email.trim()),
            null, null, null
        )
        cursor.use {
            return it.count > 0
        }
    }

    fun obtenerUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIOS, null, null, null, null, null, "$COL_USR_NOMBRE ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                lista.add(cursorToUsuario(c))
            }
        }
        return lista
    }

    fun obtenerUsuarioPorId(id: String): Usuario? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIOS, null, "$COL_USR_ID = ?", arrayOf(id), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToUsuario(c)
            }
        }
        return null
    }

    fun actualizarUltimoAcceso(id: String) {
        val db = writableDatabase
        val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_USR_ULTIMO_ACCESO, ahora)
        }
        db.update(TABLE_USUARIOS, cv, "$COL_USR_ID = ?", arrayOf(id))
    }

    private fun cursorToUsuario(c: android.database.Cursor): Usuario {
        return Usuario(
            id = c.getString(c.getColumnIndexOrThrow(COL_USR_ID)),
            nombre = c.getString(c.getColumnIndexOrThrow(COL_USR_NOMBRE)),
            email = c.getString(c.getColumnIndexOrThrow(COL_USR_EMAIL)),
            contrasena = c.getString(c.getColumnIndexOrThrow(COL_USR_CONTRASENA)),
            rol = RolUsuario.valueOf(c.getString(c.getColumnIndexOrThrow(COL_USR_ROL))),
            cargo = c.getString(c.getColumnIndexOrThrow(COL_USR_CARGO)),
            activo = c.getInt(c.getColumnIndexOrThrow(COL_USR_ACTIVO)) == 1,
            ultimoAcceso = c.getString(c.getColumnIndexOrThrow(COL_USR_ULTIMO_ACCESO))
        )
    }
}
