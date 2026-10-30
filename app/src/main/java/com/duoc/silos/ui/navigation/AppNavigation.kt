package com.duoc.silos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.silos.ui.screens.alertas.AlertasScreen
import com.duoc.silos.ui.screens.auth.LoginScreen
import com.duoc.silos.ui.screens.auth.RegistroScreen
import com.duoc.silos.ui.screens.crear.CrearSiloScreen
import com.duoc.silos.ui.screens.dashboard.DashboardScreen
import com.duoc.silos.ui.screens.despacho.DespachoTolvaScreen
import com.duoc.silos.ui.screens.detalle.DetalleSiloScreen
import com.duoc.silos.ui.screens.jefatura.PlanificacionScreen
import com.duoc.silos.ui.screens.movimiento.RegistrarMovimientoScreen
import com.duoc.silos.ui.screens.perfil.PerfilScreen
import com.duoc.silos.ui.viewmodel.SilosViewModel

@Composable
fun AppNavigation(
    viewModel: SilosViewModel
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeUsuario) {
        uiState.mensajeUsuario?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Login.ruta
            ) {
                // Pantalla de Inicio de Sesión (Login)
                composable(Screen.Login.ruta) {
                    LoginScreen(
                        cargando = uiState.cargandoAuth,
                        error = uiState.errorAuth,
                        onIniciarSesion = { email, pass ->
                            viewModel.iniciarSesion(email, pass) {
                                navController.navigate(Screen.Dashboard.ruta) {
                                    popUpTo(Screen.Login.ruta) { inclusive = true }
                                }
                            }
                        },
                        onAccesoRapido = { rol ->
                            viewModel.iniciarSesionRapida(rol) {
                                navController.navigate(Screen.Dashboard.ruta) {
                                    popUpTo(Screen.Login.ruta) { inclusive = true }
                                }
                            }
                        },
                        onIrARegistro = {
                            viewModel.limpiarErrorAuth()
                            navController.navigate(Screen.Registro.ruta)
                        },
                        onLimpiarError = {
                            viewModel.limpiarErrorAuth()
                        }
                    )
                }

                // Pantalla de Registro de Nuevo Usuario
                composable(Screen.Registro.ruta) {
                    RegistroScreen(
                        cargando = uiState.cargandoAuth,
                        error = uiState.errorAuth,
                        onVolver = {
                            viewModel.limpiarErrorAuth()
                            navController.popBackStack()
                        },
                        onRegistrar = { nom, email, pass, rol, cargo ->
                            viewModel.registrarUsuario(nom, email, pass, rol, cargo) {
                                navController.navigate(Screen.Dashboard.ruta) {
                                    popUpTo(Screen.Login.ruta) { inclusive = true }
                                }
                            }
                        },
                        onLimpiarError = {
                            viewModel.limpiarErrorAuth()
                        }
                    )
                }

                // Pantalla Principal (Dashboard)
                composable(Screen.Dashboard.ruta) {
                    DashboardScreen(
                        state = uiState,
                        onCambiarRol = { viewModel.cambiarRol(it) },
                        onFiltrarEstado = { viewModel.filtrarPorEstado(it) },
                        onSiloClick = { siloId ->
                            viewModel.seleccionarSilo(siloId)
                            navController.navigate(Screen.DetalleSilo.crearRuta(siloId))
                        },
                        onRegistrarMovimientoClick = { siloId ->
                            viewModel.seleccionarSilo(siloId)
                            navController.navigate(Screen.RegistrarMovimiento.crearRuta(siloId))
                        },
                        onCrearSiloClick = {
                            navController.navigate(Screen.CrearSilo.ruta)
                        },
                        onDespachoTolvaClick = { siloId ->
                            if (uiState.rolActivo != com.duoc.silos.data.model.RolUsuario.OPERARIO) {
                                navController.navigate(Screen.DespachoTolva.crearRuta(siloId))
                            }
                        },
                        onVerAlertasClick = {
                            navController.navigate(Screen.Alertas.ruta)
                        },
                        onVerPlanificacionClick = {
                            if (uiState.rolActivo != com.duoc.silos.data.model.RolUsuario.OPERARIO) {
                                navController.navigate(Screen.PlanificacionJefatura.ruta)
                            }
                        },
                        onSincronizar = {
                            viewModel.sincronizarConApi()
                        },
                        onVerPerfilClick = {
                            navController.navigate(Screen.Perfil.ruta)
                        },
                        onCerrarSesion = {
                            viewModel.cerrarSesion {
                                navController.navigate(Screen.Login.ruta) {
                                    popUpTo(Screen.Dashboard.ruta) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                // Detalle del Silo
                composable(
                    route = Screen.DetalleSilo.ruta,
                    arguments = listOf(navArgument("siloId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val siloId = backStackEntry.arguments?.getString("siloId") ?: ""
                    val silo = uiState.silos.find { it.id == siloId } ?: uiState.siloSeleccionado

                    DetalleSiloScreen(
                        silo = silo,
                        movimientos = uiState.movimientos,
                        rolActivo = uiState.rolActivo,
                        onVolver = { navController.popBackStack() },
                        onRegistrarMovimiento = { id ->
                            navController.navigate(Screen.RegistrarMovimiento.crearRuta(id))
                        },
                        onDespachoTolva = { id ->
                            navController.navigate(Screen.DespachoTolva.crearRuta(id))
                        }
                    )
                }

                // Registro de Movimiento (Carga / Descarga / Consumo con Foto y GPS)
                composable(
                    route = Screen.RegistrarMovimiento.ruta,
                    arguments = listOf(navArgument("siloId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val siloIdInicial = backStackEntry.arguments?.getString("siloId")

                    RegistrarMovimientoScreen(
                        silos = uiState.silos,
                        siloIdInicial = siloIdInicial,
                        operarioDefault = uiState.usuarioActual?.nombre ?: "",
                        onVolver = { navController.popBackStack() },
                        onGuardarMovimiento = { siloId, tipo, cantidad, operario, obs, lat, lon, foto, patente, guia ->
                            viewModel.registrarMovimiento(
                                siloId = siloId,
                                tipo = tipo,
                                cantidadKg = cantidad,
                                operario = operario,
                                observaciones = obs,
                                latitud = lat,
                                longitud = lon,
                                fotoUri = foto,
                                patente = patente,
                                guiaDespacho = guia
                            ) {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                // Crear Nuevo Silo desde la App
                composable(Screen.CrearSilo.ruta) {
                    val granjas = uiState.silos.map { it.granja }.distinct()

                    CrearSiloScreen(
                        granjasExistentes = granjas,
                        onVolver = { navController.popBackStack() },
                        onGuardarSilo = { id, granja, sector, capacidad, nivel, alimento, consumo, lat, lon, fotoUri ->
                            viewModel.crearNuevoSilo(
                                id = id,
                                granja = granja,
                                sectorPabellon = sector,
                                capacidadKg = capacidad,
                                nivelInicialKg = nivel,
                                tipoAlimento = alimento,
                                consumoHora = consumo,
                                latitud = lat,
                                longitud = lon,
                                fotoSeccionUri = fotoUri
                            ) {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                // Despacho de Camión Tolva (Flota y Logística)
                composable(
                    route = Screen.DespachoTolva.ruta,
                    arguments = listOf(navArgument("siloId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val siloId = backStackEntry.arguments?.getString("siloId") ?: ""
                    val silo = uiState.silos.find { it.id == siloId } ?: uiState.siloSeleccionado

                    DespachoTolvaScreen(
                        silo = silo,
                        onVolver = { navController.popBackStack() },
                        onConfirmarDespacho = { id, cantidad, chofer, patente, eta ->
                            viewModel.coordinarReposicionConTolva(id, cantidad, chofer, patente, eta) {
                                navController.popBackStack()
                            }
                        }
                    )
                }

                // Centro de Alertas
                composable(Screen.Alertas.ruta) {
                    AlertasScreen(
                        alertas = uiState.alertas,
                        onVolver = { navController.popBackStack() },
                        onAtenderAlerta = { alertaId ->
                            viewModel.marcarAlertaAtendida(alertaId)
                        },
                        onDespachoTolva = { siloId ->
                            navController.navigate(Screen.DespachoTolva.crearRuta(siloId))
                        }
                    )
                }

                // Planificación Macro (Jefatura)
                composable(Screen.PlanificacionJefatura.ruta) {
                    PlanificacionScreen(
                        silos = uiState.silos,
                        movimientos = uiState.movimientos,
                        ordenes = uiState.ordenesDespacho,
                        onVolver = { navController.popBackStack() }
                    )
                }

                // Perfil de Usuario con Cámara y Galería (Guía 13)
                composable(Screen.Perfil.ruta) {
                    PerfilScreen(
                        usuarioActual = uiState.usuarioActual,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
