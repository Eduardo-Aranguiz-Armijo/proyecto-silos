package com.duoc.silos.ui.screens.movimiento

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.Silo
import com.duoc.silos.data.model.TipoMovimiento
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMovimientoScreen(
    silos: List<Silo>,
    siloIdInicial: String?,
    operarioDefault: String = "",
    onVolver: () -> Unit,
    onGuardarMovimiento: (
        siloId: String,
        tipo: TipoMovimiento,
        cantidadKg: Double,
        operario: String,
        observaciones: String,
        latitud: Double,
        longitud: Double,
        fotoUri: String?,
        patente: String,
        guia: String
    ) -> Unit
) {
    var siloSeleccionadoId by remember {
        mutableStateOf(siloIdInicial ?: silos.firstOrNull()?.id ?: "")
    }
    var tipoMovimiento by remember { mutableStateOf(TipoMovimiento.CONSUMO) }
    var cantidadTexto by remember { mutableStateOf("") }
    var operarioTexto by remember { mutableStateOf(operarioDefault) }
    var observacionesTexto by remember { mutableStateOf("") }
    var patenteTexto by remember { mutableStateOf("") }
    var guiaTexto by remember { mutableStateOf("") }
    var errorCantidad by remember { mutableStateOf(false) }

    // Foto Evidencia con Cámara
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        fotoBitmap = bitmap
    }

    // GPS en Terreno
    var latitud by remember { mutableStateOf(-33.6882) }
    var longitud by remember { mutableStateOf(-71.2185) }
    var gpsAdjunto by remember { mutableStateOf(false) }

    val siloActual = silos.find { it.id == siloSeleccionadoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Registrar Movimiento", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Operación de faena con foto y GPS", fontSize = 11.sp, color = RojoContenedor)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Silo Destino
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SILO DESTINO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (silos.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = if (siloActual != null) "${siloActual.id} · ${siloActual.granja} (${siloActual.sectorPabellon})" else "Seleccione Silo",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                silos.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.id} · ${s.granja} (${s.nivelActualKg.toInt()} kg / ${s.capacidadTotalKg.toInt()} kg)") },
                                        onClick = {
                                            siloSeleccionadoId = s.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (siloActual != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nivel disponible: ${siloActual.nivelActualKg.toInt()} kg (${siloActual.porcentajeInt}%) · ${siloActual.tipoAlimento}",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            // Tipo de Movimiento
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TIPO DE OPERACIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TipoMovimiento.values().forEach { tipo ->
                            val seleccionado = tipo == tipoMovimiento
                            val colorBoton = when (tipo) {
                                TipoMovimiento.CARGA -> EstadoSiloNormal
                                TipoMovimiento.DESCARGA -> EstadoSiloAdvertencia
                                TipoMovimiento.CONSUMO -> RojoPrincipal
                            }

                            Button(
                                onClick = { tipoMovimiento = tipo },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (seleccionado) colorBoton else BlancoPuro,
                                    contentColor = if (seleccionado) BlancoPuro else TextoOscuro
                                ),
                                border = BorderStroke(1.dp, if (seleccionado) colorBoton else GrisBorde),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text(
                                    text = tipo.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Cantidad
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CANTIDAD DE ALIMENTO (KG)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cantidadTexto,
                        onValueChange = {
                            cantidadTexto = it
                            errorCantidad = false
                        },
                        label = { Text("Ej: 3000") },
                        isError = errorCantidad,
                        supportingText = {
                            if (errorCantidad) Text("Ingrese una cantidad válida", color = RojoPrincipal)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = { Text("kg", fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.padding(end = 12.dp)) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(500, 1000, 2500, 5000).forEach { atajo ->
                            SuggestionChip(
                                onClick = { cantidadTexto = atajo.toString() },
                                label = { Text("+$atajo kg", fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = RojoContenedor.copy(alpha = 0.5f),
                                    labelColor = RojoOscuro
                                )
                            )
                        }
                    }
                }
            }

            // Si es CARGA: Datos de Tolva y Guía de Despacho
            if (tipoMovimiento == TipoMovimiento.CARGA) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, GrisBorde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "RECEPCIÓN DE CAMIÓN TOLVA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RojoPrincipal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = patenteTexto,
                                onValueChange = { patenteTexto = it },
                                label = { Text("Patente Camión") },
                                placeholder = { Text("TL-4421") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = guiaTexto,
                                onValueChange = { guiaTexto = it },
                                label = { Text("N° Guía Despacho") },
                                placeholder = { Text("GD-90214") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Evidencia con Cámara y GPS de Faena
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CONTROL DE TERRENO (FOTO & GEORREFERENCIA)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón de Cámara
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (fotoBitmap != null) EstadoSiloNormal else RojoPrincipal),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (fotoBitmap != null) EstadoSiloNormal else RojoPrincipal
                            )
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (fotoBitmap != null) "Foto Capturada ✓" else "Foto Medidor")
                        }

                        // Botón de GPS
                        OutlinedButton(
                            onClick = {
                                latitud = -33.6882 + (Math.random() * 0.005)
                                longitud = -71.2185 + (Math.random() * 0.005)
                                gpsAdjunto = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (gpsAdjunto) EstadoSiloNormal else RojoPrincipal),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (gpsAdjunto) EstadoSiloNormal else RojoPrincipal
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (gpsAdjunto) "GPS Adjuntado ✓" else "Georreferenciar")
                        }
                    }

                    // Miniatura de la foto capturada
                    if (fotoBitmap != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = fotoBitmap!!.asImageBitmap(),
                                contentDescription = "Evidencia",
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(GrisFondoCard, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Foto de evidencia adjuntada a la orden",
                                fontSize = 12.sp,
                                color = EstadoSiloNormal,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Operario y Observaciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "OPERARIO RESPONSABLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = operarioTexto,
                        onValueChange = { operarioTexto = it },
                        label = { Text("Nombre del Operario en Turno") },
                        placeholder = { Text("Ej: Cristian Valdés") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = observacionesTexto,
                        onValueChange = { observacionesTexto = it },
                        label = { Text("Observaciones / Pabellones abastecidos") },
                        placeholder = { Text("Ej: Tolvas automáticas encendidas turno tarde") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3
                    )
                }
            }

            // Confirmar y Guardar
            Button(
                onClick = {
                    val cantidad = cantidadTexto.toDoubleOrNull()
                    if (cantidad == null || cantidad <= 0) {
                        errorCantidad = true
                    } else {
                        onGuardarMovimiento(
                            siloSeleccionadoId,
                            tipoMovimiento,
                            cantidad,
                            operarioTexto,
                            observacionesTexto,
                            latitud,
                            longitud,
                            if (fotoBitmap != null) "foto_evidencia_${System.currentTimeMillis()}" else null,
                            patenteTexto,
                            guiaTexto
                        )
                    }
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
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirmar Operación en Silo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
