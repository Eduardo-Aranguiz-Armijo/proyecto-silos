package com.duoc.silos.ui.screens.despacho

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.Silo
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespachoTolvaScreen(
    silo: Silo?,
    onVolver: () -> Unit,
    onConfirmarDespacho: (siloId: String, cantidadKg: Double, chofer: String, patente: String, eta: String) -> Unit
) {
    val context = LocalContext.current

    var cantidadKgTexto by remember {
        mutableStateOf(silo?.kgFaltantesParaLleno?.toInt()?.toString() ?: "15000")
    }
    var conductorTexto by remember { mutableStateOf("Marcos Silva") }
    var patenteTexto by remember { mutableStateOf("TL-8821") }
    var etaTexto by remember { mutableStateOf("45 minutos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Despacho de Camión Tolva", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Logística y abastecimiento en faena", fontSize = 11.sp, color = RojoContenedor)
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
        if (silo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Silo no encontrado", color = TextoSecundario)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tarjeta del Silo a Reponer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, RojoPrincipal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${silo.granja} · ${silo.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = silo.sectorPabellon,
                                    fontSize = 12.sp,
                                    color = TextoSecundario
                                )
                            }
                            Surface(
                                color = if (silo.estado == com.duoc.silos.data.model.EstadoSilo.CRITICO) RojoPrincipal.copy(alpha = 0.15f) else EstadoSiloAdvertencia.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${silo.porcentajeInt}% STOCK",
                                    color = if (silo.estado == com.duoc.silos.data.model.EstadoSilo.CRITICO) RojoPrincipal else EstadoSiloAdvertencia,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Alimento requerido: ${silo.tipoAlimento}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextoOscuro
                        )
                        Text(
                            text = "Nivel actual: ${silo.nivelActualKg.toInt()} kg / ${silo.capacidadTotalKg.toInt()} kg (Espacio disponible: ${silo.kgFaltantesParaLleno.toInt()} kg)",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }

                // GPS y Navegación para el Conductor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DESTINO GPS PARA CONDUCTOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RojoPrincipal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Coordenadas fijadas: ${silo.latitud}, ${silo.longitud}",
                            fontSize = 12.sp,
                            color = TextoOscuro
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("geo:${silo.latitud},${silo.longitud}?q=${silo.latitud},${silo.longitud}(${Uri.encode(silo.granja + " " + silo.id)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RojoPrincipal),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abrir Ruta en Google Maps / Waze")
                        }
                    }
                }

                // Datos de la Orden de Despacho
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DETALLES DE LA TOLVA Y CONDUCTOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RojoPrincipal
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = cantidadKgTexto,
                            onValueChange = { cantidadKgTexto = it },
                            label = { Text("Kilogramos a Cargar") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = { Text("kg", fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.padding(end = 8.dp)) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = conductorTexto,
                                onValueChange = { conductorTexto = it },
                                label = { Text("Conductor Asignado") },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = patenteTexto,
                                onValueChange = { patenteTexto = it },
                                label = { Text("Patente Tolva") },
                                modifier = Modifier.weight(0.8f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = etaTexto,
                            onValueChange = { etaTexto = it },
                            label = { Text("Tiempo Estimado de Arribo (ETA)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Botón Compartir Orden por WhatsApp / Logística
                OutlinedButton(
                    onClick = {
                        val mensaje = """
                            *ORDEN DE DESPACHO - ALIMENTO EN SILO*
                            Plantel: ${silo.granja} (${silo.sectorPabellon})
                            Silo: ${silo.id}
                            Alimento: ${silo.tipoAlimento}
                            Cantidad: ${cantidadKgTexto} kg
                            Conductor: $conductorTexto (Patente $patenteTexto)
                            ETA: $etaTexto
                            Coordenadas: https://maps.google.com/?q=${silo.latitud},${silo.longitud}
                        """.trimIndent()

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, mensaje)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Enviar Orden Logística"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, EstadoSiloNormal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EstadoSiloNormal)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir Orden con Conductor (WhatsApp / Mensaje)")
                }

                // Confirmar Despacho
                Button(
                    onClick = {
                        val cantidad = cantidadKgTexto.toDoubleOrNull() ?: 10000.0
                        onConfirmarDespacho(
                            silo.id,
                            cantidad,
                            conductorTexto,
                            patenteTexto,
                            etaTexto
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoPrincipal,
                        contentColor = BlancoPuro
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar y Despachar Tolva", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
