package com.duoc.silos.data.model

enum class EstadoSilo {
    NORMAL,
    ADVERTENCIA,
    CRITICO
}

enum class TipoMovimiento {
    CARGA,
    DESCARGA,
    CONSUMO
}

enum class RolUsuario(val titulo: String, val descripcion: String) {
    OPERARIO("Operario Terreno", "Registro visual de silos, evidencia con foto y GPS en faena"),
    SUPERVISOR("Supervisor Planta", "Telemetría multi-silo, geovallas y despacho de tolvas"),
    JEFATURA("Jefatura Logística", "Visión macro, balance de stock y predicción de consumo")
}

data class Silo(
    val id: String,
    val fotoSeccionUri: String? = "placeholder",
    val granja: String,
    val sectorPabellon: String,
    val capacidadTotalKg: Double,
    val nivelActualKg: Double,
    val tipoAlimento: String,
    val fechaActualizacion: String,
    val estado: EstadoSilo,
    val latitud: Double,
    val longitud: Double,
    val consumoPromedioKgHora: Double = 110.0,
    val temperaturaInternaC: Double = 22.4,
    val humedadRelativa: Double = 58.0
) {
    val porcentajeDisponible: Float
        get() = if (capacidadTotalKg > 0) ((nivelActualKg / capacidadTotalKg).toFloat().coerceIn(0f, 1f)) else 0f

    val porcentajeInt: Int
        get() = (porcentajeDisponible * 100).toInt()

    val horasAutonomia: Double
        get() = if (consumoPromedioKgHora > 0) (nivelActualKg / consumoPromedioKgHora) else 999.0

    val diasAutonomia: Double
        get() = horasAutonomia / 24.0

    val kgFaltantesParaLleno: Double
        get() = (capacidadTotalKg - nivelActualKg).coerceAtLeast(0.0)
}

data class Movimiento(
    val id: String,
    val siloId: String,
    val granja: String,
    val tipo: TipoMovimiento,
    val cantidadKg: Double,
    val fechaHora: String,
    val operario: String,
    val observaciones: String,
    val latitud: Double = -33.6853,
    val longitud: Double = -71.2144,
    val evidenciaFotoUri: String? = null,
    val patenteCamion: String = "",
    val numeroGuia: String = ""
)

data class Alerta(
    val id: String,
    val siloId: String,
    val granja: String,
    val mensaje: String,
    val estado: EstadoSilo,
    val fechaHora: String,
    val atendida: Boolean = false,
    val horasEstimadasAgotamiento: Int = 12
)

data class OrdenDespacho(
    val id: String,
    val siloId: String,
    val granja: String,
    val cantidadSolicitadaKg: Double,
    val tipoAlimento: String,
    val fechaHoraSolicitud: String,
    val conductor: String,
    val patenteTolva: String,
    val tiempoEstimadoLlegada: String,
    val estado: String = "EN_TRANSITO"
)

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: RolUsuario,
    val cargo: String,
    val activo: Boolean = true,
    val ultimoAcceso: String? = null
)

