package com.duoc.silos.ui.screens.detalle

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.R
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.data.model.Movimiento
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSiloScreen(
    silo: Silo?,
    movimientos: List<Movimiento>,
    rolActivo: RolUsuario,
    onVolver: () -> Unit,
    onRegistrarMovimiento: (String) -> Unit,
    onDespachoTolva: (String) -> Unit
) {
    val context = LocalContext.current

    // Guía 12: Animación fluida del medidor de porcentaje de alimento
    var animacionIniciada by remember { mutableStateOf(false) }
    val porcentajeAnimado by animateFloatAsState(
        targetValue = if (animacionIniciada && silo != null) silo.porcentajeDisponible else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progreso_silo"
    )

    LaunchedEffect(silo?.id) {
        animacionIniciada = true
    }

    val bitmapFoto: Bitmap? = remember(silo?.fotoSeccionUri) {
        val uriStr = silo?.fotoSeccionUri
        if (!uriStr.isNullOrBlank() && uriStr != "placeholder") {
            try {
                val uri = Uri.parse(uriStr)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (silo != null) "Silo ${silo.id}" else "Detalle de Silo",
                            fontWeight = FontWeight.Bold,
                            color = BlancoPuro
                        )
                        Text(
                            text = silo?.granja ?: "Telemetría en tiempo real",
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
                actions = {
                    if (silo != null) {
                        IconButton(onClick = {
                            val uri = Uri.parse("geo:${silo.latitud},${silo.longitud}?q=${silo.latitud},${silo.longitud}(${Uri.encode(silo.granja + " " + silo.id)})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }) {
                            Icon(Icons.Default.Navigation, contentDescription = "Mapa", tint = BlancoPuro)
                        }
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
        if (silo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró la información del silo.", color = TextoSecundario)
            }
        } else {
            val colorEstado = when (silo.estado) {
                EstadoSilo.NORMAL -> EstadoSiloNormal
                EstadoSilo.ADVERTENCIA -> EstadoSiloAdvertencia
                EstadoSilo.CRITICO -> EstadoSiloCritico
            }

            val movimientosDelSilo = movimientos.filter { it.siloId == silo.id }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tarjeta Principal con Indicador Gráfico y Datos Clave
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                        border = BorderStroke(1.dp, if (silo.estado == EstadoSilo.CRITICO) RojoPrincipal else GrisBorde),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                    Text(
                                        text = "${silo.granja} · ${silo.sectorPabellon}",
                                        fontSize = 12.sp,
                                        color = TextoSecundario,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = silo.tipoAlimento,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = EstadoSiloNormal,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Registro Visual: Certificado",
                                            fontSize = 11.sp,
                                            color = EstadoSiloNormal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Surface(
                                    color = colorEstado.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, colorEstado)
                                ) {
                                    Text(
                                        text = silo.estado.name,
                                        color = colorEstado,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Gauge / Tanque de Visualización
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(RojoContenedor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${silo.porcentajeInt}%",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colorEstado
                                )
                                Text(
                                    text = "DISPONIBILIDAD DE ALIMENTO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoSecundario,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { porcentajeAnimado },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = colorEstado,
                                    trackColor = GrisBorde
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Nivel: ${(silo.capacidadTotalKg * porcentajeAnimado).toInt()} kg",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        text = "Capacidad: ${silo.capacidadTotalKg.toInt()} kg",
                                        fontSize = 13.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Guía 12: Proyección de Autonomía con AnimatedVisibility
                            val autonomiaTexto = if (silo.horasAutonomia < 24) {
                                "⚠️ ALERTA: Quedan ~${silo.horasAutonomia.toInt()} hrs de alimento antes del desabastecimiento."
                            } else {
                                "✓ Autonomía proyectada: %.1f días al ritmo actual de consumo (%.0f kg/hora).".format(silo.diasAutonomia, silo.consumoPromedioKgHora)
                            }

                            AnimatedVisibility(
                                visible = animacionIniciada,
                                enter = fadeIn(tween(700)) + expandVertically()
                            ) {
                                Text(
                                    text = autonomiaTexto,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (silo.horasAutonomia < 24) RojoPrincipal else EstadoSiloNormal
                                )
                            }
                        }
                    }
                }

                // Registro Visual del Silo (Lugar Físico)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                        border = BorderStroke(1.dp, GrisBorde),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = RojoPrincipal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "REGISTRO VISUAL DEL LUGAR FÍSICO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RojoPrincipal
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Evidencia fotográfica de la sección del silo en faena para certificar su ubicación real:",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, GrisBorde, RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (bitmapFoto != null) {
                                    Image(
                                        bitmap = bitmapFoto.asImageBitmap(),
                                        contentDescription = "Fotografía de sección física del silo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_silo_default),
                                        contentDescription = "Silo preexistente sin fotografía nueva (NO IMAGE)",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                }

                // Telemetría de Sensores y Ubicación GPS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                        border = BorderStroke(1.dp, GrisBorde),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "TELEMETRÍA DE SENSORES Y UBICACIÓN GPS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RojoPrincipal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Temperatura", fontSize = 11.sp, color = TextoSecundario)
                                    Text("${silo.temperaturaInternaC}°C", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Humedad", fontSize = 11.sp, color = TextoSecundario)
                                    Text("${silo.humedadRelativa.toInt()}% HR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Tasa Consumo", fontSize = 11.sp, color = TextoSecundario)
                                    Text("${silo.consumoPromedioKgHora.toInt()} kg/h", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Faltan para Lleno", fontSize = 11.sp, color = TextoSecundario)
                                    Text("${silo.kgFaltantesParaLleno.toInt()} kg", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RojoPrincipal)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    val uri = Uri.parse("geo:${silo.latitud},${silo.longitud}?q=${silo.latitud},${silo.longitud}(${Uri.encode(silo.granja + " " + silo.id)})")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, RojoPrincipal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Navegar al Silo en Waze / Google Maps")
                            }
                        }
                    }
                }

                // Botones de Acción
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onRegistrarMovimiento(silo.id) },
                            modifier = if (rolActivo == RolUsuario.OPERARIO) Modifier.fillMaxWidth() else Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RojoPrincipal,
                                contentColor = BlancoPuro
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (rolActivo == RolUsuario.OPERARIO) "Registrar Movimiento (Foto + GPS)" else "Movimiento")
                        }

                        // Despacho de Tolva: Exclusivo para Supervisión y Jefatura
                        if (rolActivo != RolUsuario.OPERARIO) {
                            Button(
                                onClick = { onDespachoTolva(silo.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (silo.estado == EstadoSilo.CRITICO) RojoOscuro else RojoPrincipal,
                                    contentColor = BlancoPuro
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Despachar Tolva")
                            }
                        }
                    }

                    if (rolActivo == RolUsuario.OPERARIO) {
                        Text(
                            text = "ℹ️ Perfil Operario Terreno: Autorizado para registro visual y control de consumo en faena. La coordinación de camiones tolva es gestionada por Supervisión y Jefatura.",
                            fontSize = 11.sp,
                            color = TextoSecundario,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Historial de Movimientos de este Silo
                item {
                    Text(
                        text = "Auditoría de Cargas y Consumo (${movimientosDelSilo.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (movimientosDelSilo.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                            border = BorderStroke(1.dp, GrisBorde)
                        ) {
                            Text(
                                text = "No hay operaciones registradas para este silo.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 13.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                } else {
                    items(movimientosDelSilo) { mov ->
                        MovimientoItemCard(mov)
                    }
                }
            }
        }
    }
}

@Composable
fun MovimientoItemCard(mov: Movimiento) {
    val (colorBadge, signo) = when (mov.tipo) {
        TipoMovimiento.CARGA -> Pair(EstadoSiloNormal, "+")
        TipoMovimiento.DESCARGA -> Pair(EstadoSiloAdvertencia, "-")
        TipoMovimiento.CONSUMO -> Pair(RojoPrincipal, "-")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
        border = BorderStroke(1.dp, GrisBorde),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = colorBadge.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mov.tipo.name,
                            color = colorBadge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mov.fechaHora,
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Operario: ${mov.operario}",
                    fontSize = 12.sp,
                    color = TextoOscuro
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
                text = "$signo${mov.cantidadKg.toInt()} kg",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorBadge
            )
        }
    }
}
