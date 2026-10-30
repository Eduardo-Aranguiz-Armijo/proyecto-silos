package com.duoc.silos.data.repository

import android.content.Context
import com.duoc.silos.data.local.SilosDbHelper
import com.duoc.silos.data.model.Alerta
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.OrdenDespacho
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.Usuario
import com.duoc.silos.data.remote.MockSilosApiService
import com.duoc.silos.data.remote.SilosApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Repositorio Offline-First que coordina la persistencia en SQLite local,
 * la sesión de usuarios y el consumo de la API remota simulada para la plataforma AgroFeed 360.
 */
class SilosRepository(
    private val context: Context,
    private val apiService: SilosApiService = MockSilosApiService()
) {
    private val dbHelper = SilosDbHelper(context)
    private val prefs = context.getSharedPreferences("silos_session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_KEY_USER_ID = "logged_user_id"
        private const val PREF_KEY_IS_LOGGED_IN = "is_logged_in"
    }

    suspend fun getSilos(): List<Silo> = withContext(Dispatchers.IO) {
        dbHelper.obtenerSilos()
    }

    suspend fun getSiloById(id: String): Silo? = withContext(Dispatchers.IO) {
        dbHelper.obtenerSiloPorId(id)
    }

    suspend fun crearSilo(silo: Silo): Boolean = withContext(Dispatchers.IO) {
        dbHelper.crearSilo(silo)
    }

    suspend fun registrarMovimiento(movimiento: Movimiento): Silo? = withContext(Dispatchers.IO) {
        val siloActualizado = dbHelper.registrarMovimiento(movimiento)
        if (siloActualizado != null) {
            try {
                apiService.enviarMovimiento(movimiento)
            } catch (_: Exception) {
                // Modo offline: ya guardado en SQLite local
            }
        }
        siloActualizado
    }

    suspend fun getMovimientos(): List<Movimiento> = withContext(Dispatchers.IO) {
        dbHelper.obtenerMovimientos()
    }

    suspend fun getAlertas(): List<Alerta> = withContext(Dispatchers.IO) {
        dbHelper.obtenerAlertas()
    }

    suspend fun marcarAlertaAtendida(alertaId: String) = withContext(Dispatchers.IO) {
        dbHelper.marcarAlertaAtendida(alertaId)
    }

    suspend fun crearOrdenDespacho(orden: OrdenDespacho) = withContext(Dispatchers.IO) {
        dbHelper.crearOrdenDespacho(orden)
    }

    suspend fun getOrdenesDespacho(): List<OrdenDespacho> = withContext(Dispatchers.IO) {
        dbHelper.obtenerOrdenes()
    }

    suspend fun sincronizarConApi(): Boolean = withContext(Dispatchers.IO) {
        try {
            val silosRemotos = apiService.fetchSilosRemotos()
            silosRemotos.isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    // ==========================================
    // GESTIÓN DE AUTENTICACIÓN Y SESIÓN
    // ==========================================

    suspend fun autenticar(email: String, contrasena: String): Result<Usuario> = withContext(Dispatchers.IO) {
        val emailTrim = email.trim()
        if (emailTrim.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("El correo electrónico no puede estar vacío"))
        }
        if (contrasena.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("La contraseña no puede estar vacía"))
        }

        val usuario = dbHelper.autenticarUsuario(emailTrim, contrasena)
        if (usuario != null) {
            guardarSesion(usuario)
            Result.success(usuario)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas. Verifique correo o contraseña."))
        }
    }

    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        contrasena: String,
        rol: RolUsuario,
        cargo: String
    ): Result<Usuario> = withContext(Dispatchers.IO) {
        val nom = nombre.trim()
        val em = email.trim()
        val car = cargo.trim()

        if (nom.length < 3) {
            return@withContext Result.failure(IllegalArgumentException("El nombre debe tener al menos 3 caracteres"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Formato de correo electrónico inválido"))
        }
        if (contrasena.length < 4) {
            return@withContext Result.failure(IllegalArgumentException("La contraseña debe tener al menos 4 caracteres"))
        }
        if (dbHelper.existeEmail(em)) {
            return@withContext Result.failure(IllegalArgumentException("Ya existe una cuenta registrada con este correo"))
        }

        val nuevoId = "USR-${System.currentTimeMillis() % 100000}"
        val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val nuevoUsuario = Usuario(
            id = nuevoId,
            nombre = nom,
            email = em,
            contrasena = contrasena,
            rol = rol,
            cargo = if (car.isNotEmpty()) car else rol.titulo,
            activo = true,
            ultimoAcceso = ahora
        )

        val insertado = dbHelper.registrarUsuario(nuevoUsuario)
        if (insertado) {
            guardarSesion(nuevoUsuario)
            Result.success(nuevoUsuario)
        } else {
            Result.failure(IllegalStateException("No se pudo registrar el usuario en la base de datos"))
        }
    }

    suspend fun getUsuarios(): List<Usuario> = withContext(Dispatchers.IO) {
        dbHelper.obtenerUsuarios()
    }

    suspend fun getUsuarioSesion(): Usuario? = withContext(Dispatchers.IO) {
        val isLoggedIn = prefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return@withContext null

        val userId = prefs.getString(PREF_KEY_USER_ID, null) ?: return@withContext null
        dbHelper.obtenerUsuarioPorId(userId)
    }

    fun guardarSesion(usuario: Usuario) {
        prefs.edit()
            .putBoolean(PREF_KEY_IS_LOGGED_IN, true)
            .putString(PREF_KEY_USER_ID, usuario.id)
            .apply()
    }

    fun cerrarSesion() {
        prefs.edit()
            .clear()
            .apply()
    }
}
