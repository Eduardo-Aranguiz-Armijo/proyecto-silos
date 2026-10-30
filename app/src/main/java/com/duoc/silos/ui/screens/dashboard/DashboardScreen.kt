package com.duoc.silos.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.R
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.data.model.Silo
import com.duoc.silos.ui.theme.*
import com.duoc.silos.ui.viewmodel.SilosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: SilosUiState,
    onCambiarRol: (RolUsuario) -> Unit,
    onFiltrarEstado: (EstadoSilo?) -> Unit,
    onSiloClick: (String) -> Unit,
    onRegistrarMovimientoClick: (String) -> Unit,
    onCrearSiloClick: () -> Unit,
    onEscanearQRClick: () -> Unit = {},
    onDespachoTolvaClick: (String) -> Unit,
    onVerAlertasClick: () -> Unit,
    onVerPlanificacionClick: () -> Unit,
    onSincronizar: () -> Unit,
    onVerPerfilClick: () -> Unit = {},
    onCerrarSesion: () -> Unit
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_silos),
                            contentDescription = "Logo Silos",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AgroFeed 360",
                                fontWeight = FontWeight.ExtraBold,
                                color = BlancoPuro,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "Monitoreo de Silos",
                                color = RojoContenedor,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Botón Sincronizar
                    IconButton(onClick = onSincronizar) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sincronizar",
                            tint = BlancoPuro
                        )
                    }

                    // Botón de Alertas con Badge
                    IconButton(onClick = onVerAlertasClick) {
                        BadgedBox(
                            badge = {
                                if (state.silosCriticos > 0) {
                                    Badge(containerColor = BlancoPuro, contentColor = RojoPrincipal) {
                                        Text(state.silosCriticos.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alertas",
                                tint = BlancoPuro
                            )
                        }
                    }

                    // Botón Perfil de Usuario (Guía 13)
                    IconButton(onClick = onVerPerfilClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Mi Perfil",
                            tint = BlancoPuro
                        )
                    }

                    // Botón Cerrar Sesión
                    IconButton(onClick = { mostrarDialogoLogout = true }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = BlancoPuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RojoPrincipal,
                    titleContentColor = BlancoPuro
                )
            )
        },
        floatingActionButton = {
            // Solo Supervisión y Jefatura pueden registrar nuevos silos
            if (state.rolActivo != RolUsuario.OPERARIO) {
                ExtendedFloatingActionButton(
                    onClick = onCrearSiloClick,
                    containerColor = RojoPrincipal,
                    contentColor = BlancoPuro,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo Silo", fontWeight = FontWeight.Bold) }
                )
            }
        },
        containerColor = BlancoFondo
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Tarjeta de Usuario Conectado
                val usuario = state.usuarioActual
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, RojoBorde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = RojoPrincipal,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = usuario?.nombre?.firstOrNull()?.uppercase() ?: "U",
                                    color = BlancoPuro,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = usuario?.nombre ?: "Usuario Conectado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextoOscuro
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = RojoContenedor,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = state.rolActivo.name,
                                        color = RojoPrincipal,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = usuario?.cargo ?: state.rolActivo.titulo,
                                fontSize = 11.sp,
                                color = TextoSecundario
                            )
                            if (usuario?.email != null) {
                                Text(
                                    text = usuario.email,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { mostrarDialogoLogout = true },
                            border = BorderStroke(1.dp, RojoBorde),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salir", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                // Selector de Rol
                SelectorDeRol(
                    rolActivo = state.rolActivo,
                    onCambiarRol = onCambiarRol
                )
            }

            item {
                // Tarjetas Resumen Ejecutivas
                ResumenMetricasCards(
                    totalSilos = state.totalSilos,
                    criticos = state.silosCriticos,
                    advertencia = state.silosAdvertencia,
                    porcentajeGlobal = state.porcentajeGlobal
                )
            }

            // Banner de Acciones Rápidas (Registro Visual + Nuevo Silo + Planificación)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.rolActivo == RolUsuario.OPERARIO) {
                            // Vista Operario: Solo acciones de campo
                            OutlinedButton(
                                onClick = onCrearSiloClick,
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, RojoPrincipal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Registro Visual Silo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onVerAlertasClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GrisBorde),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp), tint = RojoPrincipal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alertas de Faena", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else if (state.rolActivo == RolUsuario.SUPERVISOR) {
                            // Vista Supervisor: Telemetría, altas y despacho
                            OutlinedButton(
                                onClick = onCrearSiloClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, RojoPrincipal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Registro Silo", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = onVerAlertasClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GrisBorde),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp), tint = RojoPrincipal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alertas", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = onVerPlanificacionClick,
                                modifier = Modifier.weight(1.1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RojoPrincipal, contentColor = BlancoPuro),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Balance Global", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            // Vista Jefatura: Visión macro estratégica
                            Button(
                                onClick = onVerPlanificacionClick,
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RojoPrincipal, contentColor = BlancoPuro),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Planificación & Balance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onCrearSiloClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, RojoPrincipal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nuevo Silo", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = onVerAlertasClick,
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GrisBorde),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp), tint = RojoPrincipal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alertas", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item {
                // Filtros de estado de silo
                FiltrosEstadoChips(
                    filtroActivo = state.filtroEstado,
                    onSeleccionarFiltro = onFiltrarEstado
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Red de Silos Conectados (${state.silosFiltrados.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro
                    )
                    Text(
                        text = "GPS & Telemetría en Línea",
                        fontSize = 11.sp,
                        color = EstadoSiloNormal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            items(state.silosFiltrados, key = { it.id }) { silo ->
                SiloCardItem(
                    silo = silo,
                    rolActivo = state.rolActivo,
                    onDetalleClick = { onSiloClick(silo.id) },
                    onRegistrarMovimientoClick = { onRegistrarMovimientoClick(silo.id) },
                    onDespachoClick = { onDespachoTolvaClick(silo.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp)) // Espacio para el FAB
            }
        }
    }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = RojoPrincipal
                )
            },
            title = {
                Text("¿Cerrar Sesión?", fontWeight = FontWeight.Bold)
            },
            text = {
                val nombreUsr = state.usuarioActual?.nombre ?: "su cuenta"
                Text("¿Desea cerrar la sesión activa de $nombreUsr?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoLogout = false
                        onCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoPrincipal)
                ) {
                    Text("Cerrar Sesión", color = BlancoPuro)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar", color = TextoOscuro)
                }
            }
        )
    }
}

@Composable
fun SelectorDeRol(
    rolActivo: RolUsuario,
    onCambiarRol: (RolUsuario) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
        border = BorderStroke(1.dp, RojoBorde),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERFIL DE ACCESO Y PRIVILEGIOS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RojoPrincipal,
                    letterSpacing = 1.sp
                )
                Surface(
                    color = RojoContenedor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "RBAC ACTIVO",
                        color = RojoPrincipal,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RolUsuario.values().forEach { rol ->
                    val seleccionado = rol == rolActivo
                    FilterChip(
                        selected = seleccionado,
                        onClick = { onCambiarRol(rol) },
                        label = {
                            Text(
                                text = rol.titulo,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RojoPrincipal,
                            selectedLabelColor = BlancoPuro,
                            containerColor = BlancoPuro,
                            labelColor = TextoOscuro
                        ),
                        border = BorderStroke(1.dp, if (seleccionado) RojoPrincipal else GrisBorde)
                    )
                }
            }
            Text(
                text = rolActivo.descripcion,
                fontSize = 11.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Resumen de Permisos según el rol seleccionado
            Spacer(modifier = Modifier.height(6.dp))
            val permisosTexto = when (rolActivo) {
                RolUsuario.OPERARIO -> "• Permisos: Registro visual de silos, control de cargas/consumos con Foto y GPS en faena. (Alta de silos y despacho tolva restringidos)."
                RolUsuario.SUPERVISOR -> "• Permisos: Telemetría multi-silo en tiempo real, resolución de alertas de stock, alta de silos y coordinación de camiones tolva."
                RolUsuario.JEFATURA -> "• Permisos: Balance global de inventario, proyección de agotamiento por días, planificación de compras y despacho de flota."
            }
            Text(
                text = permisosTexto,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (rolActivo == RolUsuario.OPERARIO) Color(0xFFC62828) else Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun ResumenMetricasCards(
    totalSilos: Int,
    criticos: Int,
    advertencia: Int,
    porcentajeGlobal: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricaCardItem(
            modifier = Modifier.weight(1f),
            titulo = "Silos Activos",
            valor = totalSilos.toString(),
            colorFondo = BlancoSuperficie,
            colorBorde = GrisBorde,
            colorValor = TextoOscuro
        )
        MetricaCardItem(
            modifier = Modifier.weight(1f),
            titulo = "Riesgo / Reposición",
            valor = (criticos + advertencia).toString(),
            colorFondo = if (criticos > 0) RojoContenedor else BlancoSuperficie,
            colorBorde = if (criticos > 0) RojoPrincipal else GrisBorde,
            colorValor = if (criticos > 0) RojoPrincipal else TextoOscuro,
            iconoAlerta = criticos > 0
        )
        MetricaCardItem(
            modifier = Modifier.weight(1f),
            titulo = "Stock Global",
            valor = "$porcentajeGlobal%",
            colorFondo = BlancoSuperficie,
            colorBorde = GrisBorde,
            colorValor = if (porcentajeGlobal < 30) RojoPrincipal else EstadoSiloNormal
        )
    }
}

@Composable
fun MetricaCardItem(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    colorFondo: Color,
    colorBorde: Color,
    colorValor: Color,
    iconoAlerta: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, colorBorde),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 10.sp,
                color = TextoSecundario,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (iconoAlerta) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = RojoPrincipal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(text = valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorValor)
            }
        }
    }
}

@Composable
fun FiltrosEstadoChips(
    filtroActivo: EstadoSilo?,
    onSeleccionarFiltro: (EstadoSilo?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = filtroActivo == null,
                onClick = { onSeleccionarFiltro(null) },
                label = { Text("Todos") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RojoPrincipal,
                    selectedLabelColor = BlancoPuro
                )
            )
        }
        items(EstadoSilo.values()) { estado ->
            val seleccionado = filtroActivo == estado
            val colorEstado = when (estado) {
                EstadoSilo.NORMAL -> EstadoSiloNormal
                EstadoSilo.ADVERTENCIA -> EstadoSiloAdvertencia
                EstadoSilo.CRITICO -> EstadoSiloCritico
            }
            FilterChip(
                selected = seleccionado,
                onClick = { onSeleccionarFiltro(estado) },
                label = { Text(estado.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorEstado,
                    selectedLabelColor = BlancoPuro
                )
            )
        }
    }
}

@Composable
fun SiloCardItem(
    silo: Silo,
    rolActivo: RolUsuario,
    onDetalleClick: () -> Unit,
    onRegistrarMovimientoClick: () -> Unit,
    onDespachoClick: () -> Unit
) {
    val colorEstado = when (silo.estado) {
        EstadoSilo.NORMAL -> EstadoSiloNormal
        EstadoSilo.ADVERTENCIA -> EstadoSiloAdvertencia
        EstadoSilo.CRITICO -> EstadoSiloCritico
    }

    val bordeCard = if (silo.estado == EstadoSilo.CRITICO) {
        BorderStroke(1.5.dp, RojoPrincipal)
    } else {
        BorderStroke(1.dp, GrisBorde)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetalleClick() },
        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
        border = bordeCard,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Cabecera de la tarjeta con ID, Granja y Badge adaptativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = silo.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextoOscuro
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = EstadoSiloNormal.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.5.dp, EstadoSiloNormal.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = EstadoSiloNormal,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Lugar Físico",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EstadoSiloNormal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${silo.granja} · ${silo.sectorPabellon}",
                        fontSize = 12.sp,
                        color = TextoSecundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = colorEstado.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colorEstado)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (silo.estado == EstadoSilo.CRITICO) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = colorEstado,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = silo.estado.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Alimento y Datos de Telemetría (Temperatura y Humedad)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = silo.tipoAlimento,
                    fontSize = 12.sp,
                    color = TextoOscuro,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${silo.temperaturaInternaC}°C · ${silo.humedadRelativa.toInt()}% HR",
                    fontSize = 11.sp,
                    color = TextoSecundario,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Barra de progreso y capacidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${silo.nivelActualKg.toInt()} kg / ${silo.capacidadTotalKg.toInt()} kg",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
                Text(
                    text = "${silo.porcentajeInt}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorEstado
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val progresoAnimado by animateFloatAsState(
                targetValue = silo.porcentajeDisponible,
                animationSpec = tween(durationMillis = 800),
                label = "progreso_${silo.id}"
            )

            LinearProgressIndicator(
                progress = { progresoAnimado },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorEstado,
                trackColor = GrisBorde
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Proyección de Autonomía y Días de Stock
            val textoAutonomia = if (silo.horasAutonomia < 24) {
                "⚠️ Autonomía estimada: ${silo.horasAutonomia.toInt()} hrs restantes"
            } else {
                "Stock proyectado: %.1f días".format(silo.diasAutonomia)
            }

            Text(
                text = textoAutonomia,
                fontSize = 11.sp,
                fontWeight = if (silo.horasAutonomia < 24) FontWeight.Bold else FontWeight.Normal,
                color = if (silo.horasAutonomia < 24) RojoPrincipal else TextoSecundario
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = RojoPrincipal, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "GPS: %.3f, %.3f".format(silo.latitud, silo.longitud),
                        fontSize = 10.sp,
                        color = TextoSecundario
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Botón Despacho Tolva: Solo habilitado para Supervisión y Jefatura
                    if (rolActivo != RolUsuario.OPERARIO && (silo.estado == EstadoSilo.CRITICO || silo.estado == EstadoSilo.ADVERTENCIA)) {
                        Button(
                            onClick = onDespachoClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RojoPrincipal,
                                contentColor = BlancoPuro
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tolva", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = onRegistrarMovimientoClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (silo.estado == EstadoSilo.CRITICO) BlancoPuro else RojoPrincipal,
                            contentColor = if (silo.estado == EstadoSilo.CRITICO) RojoPrincipal else BlancoPuro
                        ),
                        border = if (silo.estado == EstadoSilo.CRITICO) BorderStroke(1.dp, RojoPrincipal) else null,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Movimiento", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
