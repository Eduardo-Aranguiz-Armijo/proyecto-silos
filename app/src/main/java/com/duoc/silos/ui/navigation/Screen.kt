package com.duoc.silos.ui.navigation

sealed class Screen(val ruta: String, val titulo: String) {
    object Login : Screen("login", "Inicio de Sesión")
    object Registro : Screen("registro", "Registro de Usuario")
    object Dashboard : Screen("dashboard", "Monitoreo de Silos")
    object DetalleSilo : Screen("detalle/{siloId}", "Detalle del Silo") {
        fun crearRuta(siloId: String) = "detalle/$siloId"
    }
    object RegistrarMovimiento : Screen("movimiento/{siloId}", "Registrar Movimiento") {
        fun crearRuta(siloId: String) = "movimiento/$siloId"
    }
    object CrearSilo : Screen("crear_silo", "Registrar Nuevo Silo")
    object DespachoTolva : Screen("despacho_tolva/{siloId}", "Despacho de Camión Tolva") {
        fun crearRuta(siloId: String) = "despacho_tolva/$siloId"
    }
    object Alertas : Screen("alertas", "Centro de Alertas")
    object PlanificacionJefatura : Screen("planificacion", "Planificación de Abastecimiento")
    object Perfil : Screen("perfil", "Perfil de Usuario")
}
