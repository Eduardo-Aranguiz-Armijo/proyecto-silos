package com.duoc.silos.ui.screens.jefatura

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.OrdenDespacho
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(
    silos: List<Silo>,
    movimientos: List<Movimiento>,
    ordenes: List<OrdenDespacho>,
    onVolver: () -> Unit
) {
    val totalDisponible = silos.sumOf { it.nivelActualKg }
    val capacidadTotal = silos.sumOf { it.capacidadTotalKg }
    val totalConsumido = movimientos.filter { it.tipo == TipoMovimiento.CONSUMO }.sumOf { it.cantidadKg }
    val granjasAgrupadas = silos.groupBy { it.granja }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Planificación & Logística Macro",
                            fontWeight = FontWeight.Bold,
                            color = BlancoPuro
                        )
                        Text(
                            text = "Control gerencial de inventarios y flota",
                            fontSize = 11.sp,
                            color = RojoContenedor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
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
        containerColor = BlancoFondo
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta Resumen de Planificación
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, RojoPrincipal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Insights, contentDescription = null, tint = RojoPrincipal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BALANCE NACIONAL DE ALIMENTO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = RojoPrincipal,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Existencias Disponibles", fontSize = 11.sp, color = TextoSecundario)
                                Text("${totalDisponible.toInt()} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Consumo Acumulado", fontSize = 11.sp, color = TextoSecundario)
                                Text("${totalConsumido.toInt()} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RojoPrincipal)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        val porcentajeTotal = if (capacidadTotal > 0) ((totalDisponible / capacidadTotal) * 100).toInt() else 0
                        LinearProgressIndicator(
                            progress = { if (capacidadTotal > 0) (totalDisponible / capacidadTotal).toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = if (porcentajeTotal < 30) RojoPrincipal else EstadoSiloNormal,
                            trackColor = GrisBorde
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ocupación de red: $porcentajeTotal% (${capacidadTotal.toInt()} kg de capacidad instalada)",
                            fontSize = 11.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            // Órdenes de Despacho de Flota de Tolvas
            if (ordenes.isNotEmpty()) {
                item {
                    Text(
                        text = "Flota de Camiones Tolva en Ruta (${ordenes.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro
                    )
                }

                items(ordenes) { orden ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                        border = BorderStroke(1.dp, EstadoSiloNormal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = EstadoSiloNormal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = orden.id, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Surface(
                                    color = EstadoSiloNormal.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = orden.estado,
                                        color = EstadoSiloNormal,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Destino: ${orden.granja} (${orden.siloId}) · ${orden.tipoAlimento}",
                                fontSize = 12.sp,
                                color = TextoOscuro
                            )
                            Text(
                                text = "Conductor: ${orden.conductor} · Patente: ${orden.patenteTolva} · Carga: ${orden.cantidadSolicitadaKg.toInt()} kg",
                                fontSize = 11.sp,
                                color = TextoSecundario
                            )
                            Text(
                                text = "ETA: ${orden.tiempoEstimadoLlegada}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RojoPrincipal
                            )
                        }
                    }
                }
            }

            // Existencias por Plantel
            item {
                Text(
                    text = "Disponibilidad por Plantel / Fundo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextoOscuro
                )
            }

            items(granjasAgrupadas.keys.toList()) { nombreGranja ->
                val silosGranja = granjasAgrupadas[nombreGranja] ?: emptyList()
                val totalGranja = silosGranja.sumOf { it.nivelActualKg }
                val capacidadGranja = silosGranja.sumOf { it.capacidadTotalKg }
                val pctGranja = if (capacidadGranja > 0) ((totalGranja / capacidadGranja) * 100).toInt() else 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = nombreGranja, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoOscuro)
                            Text(text = "$pctGranja%", fontWeight = FontWeight.Bold, color = if (pctGranja < 25) RojoPrincipal else EstadoSiloNormal)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { if (capacidadGranja > 0) (totalGranja / capacidadGranja).toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (pctGranja < 25) RojoPrincipal else EstadoSiloNormal,
                            trackColor = GrisBorde
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${totalGranja.toInt()} kg en ${silosGranja.size} silos asignados",
                            fontSize = 11.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            // Histórico Completo de Auditoría
            item {
                Text(
                    text = "Histórico de Auditoría de Faenas (${movimientos.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextoOscuro,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(movimientos) { mov ->
                val colorTipo = when (mov.tipo) {
                    TipoMovimiento.CARGA -> EstadoSiloNormal
                    TipoMovimiento.DESCARGA -> EstadoSiloAdvertencia
                    TipoMovimiento.CONSUMO -> RojoPrincipal
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${mov.granja} · ${mov.siloId}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextoOscuro
                            )
                            Text(
                                text = "${mov.tipo.name} · ${mov.operario} · ${mov.fechaHora}",
                                fontSize = 11.sp,
                                color = TextoSecundario
                            )
                            if (mov.patenteCamion.isNotBlank()) {
                                Text(
                                    text = "Tolva: ${mov.patenteCamion} · Guía: ${mov.numeroGuia}",
                                    fontSize = 11.sp,
                                    color = TextoSecundario
                                )
                            }
                            if (mov.observaciones.isNotBlank()) {
                                Text(
                                    text = mov.observaciones,
                                    fontSize = 11.sp,
                                    color = TextoSecundario
                                )
                            }
                        }
                        Text(
                            text = "${mov.cantidadKg.toInt()} kg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colorTipo
                        )
                    }
                }
            }
        }
    }
}
