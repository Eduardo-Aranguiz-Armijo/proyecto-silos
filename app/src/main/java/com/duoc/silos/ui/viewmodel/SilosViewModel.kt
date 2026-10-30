package com.duoc.silos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.silos.data.model.Alerta
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.OrdenDespacho
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import com.duoc.silos.data.model.Usuario
import com.duoc.silos.data.repository.SilosRepository
import com.duoc.silos.notification.SiloNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SilosUiState(
    val silos: List<Silo> = emptyList(),
    val movimientos: List<Movimiento> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val ordenesDespacho: List<OrdenDespacho> = emptyList(),
    val rolActivo: RolUsuario = RolUsuario.OPERARIO,
    val filtroEstado: EstadoSilo? = null,
    val cargando: Boolean = false,
    val mensajeUsuario: String? = null,
    val siloSeleccionado: Silo? = null,
    val usuarioActual: Usuario? = null,
    val estaAutenticado: Boolean = false,
    val cargandoAuth: Boolean = false,
    val errorAuth: String? = null,
    val usuariosDisponibles: List<Usuario> = emptyList()
) {
    val silosFiltrados: List<Silo>
        get() = if (filtroEstado == null) silos else silos.filter { it.estado == filtroEstado }

    val totalSilos: Int get() = silos.size
    val silosCriticos: Int get() = silos.count { it.estado == EstadoSilo.CRITICO }
    val silosAdvertencia: Int get() = silos.count { it.estado == EstadoSilo.ADVERTENCIA }
    val totalAlimentoKg: Double get() = silos.sumOf { it.nivelActualKg }
    val capacidadTotalKg: Double get() = silos.sumOf { it.capacidadTotalKg }
    val porcentajeGlobal: Int
        get() = if (capacidadTotalKg > 0) ((totalAlimentoKg / capacidadTotalKg) * 100).toInt() else 0
}

class SilosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SilosRepository(application.applicationContext)
    private val notificationHelper = SiloNotificationHelper(application.applicationContext)

    private val _uiState = MutableStateFlow(SilosUiState())
    val uiState: StateFlow<SilosUiState> = _uiState.asStateFlow()

    init {
        verificarSesion()
        cargarDatos()
        cargarUsuariosDisponibles()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val silos = repository.getSilos()
            val movimientos = repository.getMovimientos()
            val alertas = repository.getAlertas()
            val ordenes = repository.getOrdenesDespacho()
            _uiState.update {
                it.copy(
                    silos = silos,
                    movimientos = movimientos,
                    alertas = alertas,
                    ordenesDespacho = ordenes,
                    cargando = false
                )
            }
        }
    }

    fun cambiarRol(rol: RolUsuario) {
        _uiState.update { it.copy(rolActivo = rol) }
    }

    fun filtrarPorEstado(estado: EstadoSilo?) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    fun seleccionarSilo(siloId: String) {
        viewModelScope.launch {
            val silo = repository.getSiloById(siloId)
            _uiState.update { it.copy(siloSeleccionado = silo) }
        }
    }

    fun crearNuevoSilo(
        id: String,
        granja: String,
        sectorPabellon: String,
        capacidadKg: Double,
        nivelInicialKg: Double,
        tipoAlimento: String,
        consumoHora: Double,
        latitud: Double,
        longitud: Double,
        fotoSeccionUri: String? = null,
        onExito: () -> Unit
    ) {
        if (id.isBlank() || granja.isBlank() || capacidadKg <= 0) {
            _uiState.update { it.copy(mensajeUsuario = "Complete los campos obligatorios del silo") }
            return
        }

        val porcentaje = (nivelInicialKg / capacidadKg).coerceIn(0.0, 1.0)
        val estado = when {
            porcentaje <= 0.20 -> EstadoSilo.CRITICO
            porcentaje <= 0.35 -> EstadoSilo.ADVERTENCIA
            else -> EstadoSilo.NORMAL
        }

        val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

        val nuevoSilo = Silo(
            id = id.trim().uppercase(),
            fotoSeccionUri = fotoSeccionUri,
            granja = granja.trim(),
            sectorPabellon = sectorPabellon.trim().ifBlank { "Pabellón General" },
            capacidadTotalKg = capacidadKg,
            nivelActualKg = nivelInicialKg.coerceIn(0.0, capacidadKg),
            tipoAlimento = tipoAlimento.trim().ifBlank { "Alimento Estándar" },
            fechaActualizacion = ahora,
            estado = estado,
            latitud = latitud,
            longitud = longitud,
            consumoPromedioKgHora = if (consumoHora > 0) consumoHora else 120.0,
            temperaturaInternaC = 21.0,
            humedadRelativa = 55.0
        )

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val exito = repository.crearSilo(nuevoSilo)
            if (exito) {
                val silosActualizados = repository.getSilos()
                _uiState.update {
                    it.copy(
                        silos = silosActualizados,
                        cargando = false,
                        mensajeUsuario = "Silo ${nuevoSilo.id} registrado exitosamente con Registro Visual"
                    )
                }
                onExito()
            } else {
                _uiState.update {
                    it.copy(cargando = false, mensajeUsuario = "Error al guardar el nuevo silo en la base de datos")
                }
            }
        }
    }

    fun registrarMovimiento(
        siloId: String,
        tipo: TipoMovimiento,
        cantidadKg: Double,
        operario: String,
        observaciones: String,
        latitud: Double = -33.6853,
        longitud: Double = -71.2144,
        fotoUri: String? = null,
        patente: String = "",
        guiaDespacho: String = "",
        onExito: () -> Unit
    ) {
        if (cantidadKg <= 0) {
            _uiState.update { it.copy(mensajeUsuario = "La cantidad debe ser mayor a 0 kg") }
            return
        }

        val siloActual = _uiState.value.silos.find { it.id == siloId }
        val granja = siloActual?.granja ?: "Granja Desconocida"

        val nuevoMovimiento = Movimiento(
            id = "MOV-${System.currentTimeMillis() % 100000}",
            siloId = siloId,
            granja = granja,
            tipo = tipo,
            cantidadKg = cantidadKg,
            fechaHora = "",
            operario = operario.ifBlank { "Operario de Turno" },
            observaciones = observaciones,
            latitud = latitud,
            longitud = longitud,
            evidenciaFotoUri = fotoUri,
            patenteCamion = patente,
            numeroGuia = guiaDespacho
        )

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val siloActualizado = repository.registrarMovimiento(nuevoMovimiento)

            if (siloActualizado != null) {
                if (siloActualizado.estado == EstadoSilo.CRITICO) {
                    notificationHelper.dispararAlertaSilo(siloActualizado)
                }

                val silos = repository.getSilos()
                val movimientos = repository.getMovimientos()
                val alertas = repository.getAlertas()

                _uiState.update {
                    it.copy(
                        silos = silos,
                        movimientos = movimientos,
                        alertas = alertas,
                        cargando = false,
                        siloSeleccionado = siloActualizado,
                        mensajeUsuario = "Operación registrada (${tipo.name}: $cantidadKg kg) en ${siloActualizado.id}"
                    )
                }
                onExito()
            } else {
                _uiState.update {
                    it.copy(cargando = false, mensajeUsuario = "Error al actualizar nivel en el silo")
                }
            }
        }
    }

    fun coordinarReposicionConTolva(
        siloId: String,
        cantidadKg: Double,
        conductor: String,
        patente: String,
        eta: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            val silo = repository.getSiloById(siloId)
            if (silo != null) {
                val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
                val nuevaOrden = OrdenDespacho(
                    id = "DSP-${(System.currentTimeMillis() % 9000) + 1000}",
                    siloId = silo.id,
                    granja = silo.granja,
                    cantidadSolicitadaKg = if (cantidadKg > 0) cantidadKg else silo.kgFaltantesParaLleno,
                    tipoAlimento = silo.tipoAlimento,
                    fechaHoraSolicitud = ahora,
                    conductor = conductor.ifBlank { "Conductor Tolva Asignado" },
                    patenteTolva = patente.ifBlank { "TL-2044" },
                    tiempoEstimadoLlegada = eta.ifBlank { "40 minutos" }
                )
                repository.crearOrdenDespacho(nuevaOrden)
                val ordenesActualizadas = repository.getOrdenesDespacho()
                _uiState.update {
                    it.copy(
                        ordenesDespacho = ordenesActualizadas,
                        mensajeUsuario = "Orden ${nuevaOrden.id} despachada a ${nuevaOrden.conductor} (${nuevaOrden.patenteTolva}). ETA: ${nuevaOrden.tiempoEstimadoLlegada}"
                    )
                }
                onExito()
            }
        }
    }

    fun marcarAlertaAtendida(alertaId: String) {
        viewModelScope.launch {
            repository.marcarAlertaAtendida(alertaId)
            val alertas = repository.getAlertas()
            _uiState.update {
                it.copy(alertas = alertas, mensajeUsuario = "Alerta atendida")
            }
        }
    }

    fun sincronizarConApi() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val exito = repository.sincronizarConApi()
            val silos = repository.getSilos()
            _uiState.update {
                it.copy(
                    silos = silos,
                    cargando = false,
                    mensajeUsuario = if (exito) "Conectado al servidor central AgroFeed 360" else "Modo Offline Activo (Base de datos local segura)"
                )
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeUsuario = null) }
    }

    // ==========================================
    // MÉTODOS DE AUTENTICACIÓN Y SESIÓN
    // ==========================================

    fun verificarSesion() {
        viewModelScope.launch {
            val sesionUsuario = repository.getUsuarioSesion()
            if (sesionUsuario != null) {
                _uiState.update {
                    it.copy(
                        usuarioActual = sesionUsuario,
                        rolActivo = sesionUsuario.rol,
                        estaAutenticado = true
                    )
                }
            }
        }
    }

    fun cargarUsuariosDisponibles() {
        viewModelScope.launch {
            val usuarios = repository.getUsuarios()
            _uiState.update { it.copy(usuariosDisponibles = usuarios) }
        }
    }

    fun iniciarSesion(email: String, contrasena: String, onExito: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoAuth = true, errorAuth = null) }
            val resultado = repository.autenticar(email, contrasena)
            resultado.onSuccess { usuario ->
                _uiState.update {
                    it.copy(
                        usuarioActual = usuario,
                        rolActivo = usuario.rol,
                        estaAutenticado = true,
                        cargandoAuth = false,
                        errorAuth = null,
                        mensajeUsuario = "Bienvenido/a, ${usuario.nombre}"
                    )
                }
                onExito()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        cargandoAuth = false,
                        errorAuth = ex.message ?: "Credenciales incorrectas"
                    )
                }
            }
        }
    }

    fun iniciarSesionRapida(rol: RolUsuario, onExito: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoAuth = true, errorAuth = null) }
            val usuarios = if (_uiState.value.usuariosDisponibles.isNotEmpty()) {
                _uiState.value.usuariosDisponibles
            } else {
                repository.getUsuarios()
            }
            val usuarioObjetivo = usuarios.firstOrNull { it.rol == rol }
            if (usuarioObjetivo != null) {
                val resultado = repository.autenticar(usuarioObjetivo.email, usuarioObjetivo.contrasena)
                resultado.onSuccess { usuario ->
                    _uiState.update {
                        it.copy(
                            usuarioActual = usuario,
                            rolActivo = usuario.rol,
                            estaAutenticado = true,
                            cargandoAuth = false,
                            errorAuth = null,
                            mensajeUsuario = "Sesión iniciada como ${usuario.rol.titulo}: ${usuario.nombre}"
                        )
                    }
                    onExito()
                }.onFailure { ex ->
                    _uiState.update {
                        it.copy(cargandoAuth = false, errorAuth = ex.message)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(cargandoAuth = false, errorAuth = "No se encontró usuario predeterminado para este rol")
                }
            }
        }
    }

    fun registrarUsuario(
        nombre: String,
        email: String,
        contrasena: String,
        rol: RolUsuario,
        cargo: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoAuth = true, errorAuth = null) }
            val resultado = repository.registrarUsuario(nombre, email, contrasena, rol, cargo)
            resultado.onSuccess { usuario ->
                val listaActualizada = repository.getUsuarios()
                _uiState.update {
                    it.copy(
                        usuarioActual = usuario,
                        rolActivo = usuario.rol,
                        estaAutenticado = true,
                        cargandoAuth = false,
                        errorAuth = null,
                        usuariosDisponibles = listaActualizada,
                        mensajeUsuario = "¡Cuenta creada exitosamente! Bienvenido, ${usuario.nombre}"
                    )
                }
                onExito()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        cargandoAuth = false,
                        errorAuth = ex.message ?: "Error al registrar el usuario"
                    )
                }
            }
        }
    }

    fun cerrarSesion(onLogout: () -> Unit) {
        viewModelScope.launch {
            repository.cerrarSesion()
            _uiState.update {
                it.copy(
                    usuarioActual = null,
                    estaAutenticado = false,
                    errorAuth = null,
                    mensajeUsuario = "Sesión finalizada correctamente"
                )
            }
            onLogout()
        }
    }

    fun limpiarErrorAuth() {
        _uiState.update { it.copy(errorAuth = null) }
    }
}
