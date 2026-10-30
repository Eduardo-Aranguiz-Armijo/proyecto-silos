package com.duoc.silos.data.remote

import com.duoc.silos.data.model.Alerta
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import kotlinx.coroutines.delay

/**
 * Servicio API simulado para representar la conexión con el sistema central
 * de monitoreo de silos según el requisito 3.4 y 5.1 del caso académico.
 */
interface SilosApiService {
    suspend fun fetchSilosRemotos(): List<Silo>
    suspend fun enviarMovimiento(movimiento: Movimiento): Boolean
    suspend fun fetchAlertasRemotas(): List<Alerta>
}

class MockSilosApiService : SilosApiService {

    override suspend fun fetchSilosRemotos(): List<Silo> {
        delay(600) // Simular latencia de red
        return listOf(
            Silo("SILO-01", "placeholder", "Plantel Melipilla Central", "Pabellón Broilers 1 y 2", 22000.0, 18500.0, "Iniciador Aves Premium", "26-10-2026 15:40", EstadoSilo.NORMAL, -33.6882, -71.2185, 125.0, 21.8, 54.0),
            Silo("SILO-02", "placeholder", "Plantel Melipilla Central", "Pabellón Broilers 3 y 4", 20000.0, 4200.0, "Engorde Aves Pellet", "26-10-2026 15:35", EstadoSilo.ADVERTENCIA, -33.6895, -71.2198, 140.0, 23.1, 56.5),
            Silo("SILO-03", "placeholder", "Plantel Mallarauco Aves", "Pabellón Pavos Reproductoras", 28000.0, 2600.0, "Crecimiento Pavo Extruido", "26-10-2026 15:30", EstadoSilo.CRITICO, -33.6120, -71.1890, 180.0, 24.2, 59.0),
            Silo("SILO-04", "placeholder", "Plantel Mallarauco Aves", "Pabellón Ponedoras Huevos", 16000.0, 14800.0, "Postura Alta Energía", "26-10-2026 12:15", EstadoSilo.NORMAL, -33.6135, -71.1882, 95.0, 20.9, 52.0),
            Silo("SILO-05", "placeholder", "Fundo San Pedro Ganadero", "Establo Lechero Vacuno A", 32000.0, 4800.0, "Ración Total Mezclada (TMR)", "26-10-2026 14:10", EstadoSilo.CRITICO, -33.8920, -71.4530, 250.0, 22.0, 58.0),
            Silo("SILO-06", "placeholder", "Fundo San Pedro Ganadero", "Engorde Bovino Lote Norte", 25000.0, 19200.0, "Concentrado Granulado Bovino", "26-10-2026 11:45", EstadoSilo.NORMAL, -33.8945, -71.4510, 160.0, 21.5, 55.0)
        )
    }

    override suspend fun enviarMovimiento(movimiento: Movimiento): Boolean {
        delay(500) // Simular petición POST /api/movimientos
        return true
    }

    override suspend fun fetchAlertasRemotas(): List<Alerta> {
        delay(400)
        return listOf(
            Alerta("ALT-01", "SILO-03", "Plantel Mallarauco Aves", "Nivel crítico: 9.2% disponible. Requiere reposición inmediata.", EstadoSilo.CRITICO, "26-10-2026 15:30", false, 14),
            Alerta("ALT-02", "SILO-05", "Fundo San Pedro Ganadero", "Nivel crítico: 15.0% disponible. Reposición en menos de 24 hrs.", EstadoSilo.CRITICO, "26-10-2026 14:10", false, 19),
            Alerta("ALT-03", "SILO-02", "Plantel Melipilla Central", "Advertencia: 21.0% disponible. Programar reposición.", EstadoSilo.ADVERTENCIA, "26-10-2026 15:35", false, 30)
        )
    }
}
